package it.percassi.batch.writers;

import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import it.percassi.batch.mongo.NewRelicMongoWeeklyItem;
import it.percassi.utils.PerPortalConstants;

public class NrJobWeeklyWriter implements ItemWriter<NewRelicMongoWeeklyItem> {

	@Value("${mongoDB.DBname}")
	private String mongoDBName;
	@Value("${mongoDB.URI}")
	private String mongoDBUri;

	private static final Logger LOG = LoggerFactory.getLogger(NrJobWeeklyWriter.class);
	@Override
	public void write(List<? extends NewRelicMongoWeeklyItem> item) throws Exception {

		final MongoClientURI mcu = new MongoClientURI(mongoDBUri);
		final MongoClient mc = new MongoClient(mcu);
		final MongoDatabase mdb = mc.getDatabase(mongoDBName);
		Document doc = null;
		try {
			
			doc = toBSONDoc(item.get(0));
			
			MongoCollection<Document> collection = mdb.getCollection(PerPortalConstants.NEW_RELIC_COLLECTION_WEEKLY);
			LOG.info("Saving data to mongoDB: {}", doc);
			collection.insertOne(doc);
		} 
		catch (Exception e) {
			LOG.error("An error occured while saving data {} with exception {} ",doc,e);
		}
		finally {
			mc.close();
		}
	}
	

	private Document toBSONDoc(NewRelicMongoWeeklyItem itemToConvert) {
		Document ret = new Document();
		ret.append("metricName", itemToConvert.getMetricName());
		ret.append("day",null);
		ret.append("value", itemToConvert.getValue());
		ret.append("valueName", itemToConvert.getValueName());
		ret.append("weekNumber", itemToConvert.getWeekNumber());
		return ret;
	}

}
