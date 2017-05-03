package it.percassi.batch;

import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import it.percassi.batch.nrelic.NewRelicMongoDailyItem;
import it.percassi.utils.PerPortalConstants;

public class NrJobDailyWriter implements ItemWriter<NewRelicMongoDailyItem> {

	@Value("${mongoDB.DBname}")
	private String mongoDBName;
	@Value("${mongoDB.URI}")
	private String mongoDBUri;

	private static final Logger LOG = LoggerFactory.getLogger(NrJobDailyWriter.class);

	@Override
	public void write(List<? extends NewRelicMongoDailyItem> item) throws JsonProcessingException, MongoException {
		

		final MongoClientURI mcu = new MongoClientURI(mongoDBUri);
		final MongoClient mc = new MongoClient(mcu);
		final MongoDatabase mdb = mc.getDatabase(mongoDBName);
		Document doc = null;

		try {
			
			doc = toBSONDoc(item.get(0));
			
			MongoCollection<Document> collection = mdb.getCollection(PerPortalConstants.NEW_RELIC_COLLECTION_DAILY);
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

	private Document toBSONDoc(NewRelicMongoDailyItem itemToConvert) {
		Document ret = new Document();
		ret.append("metricName", itemToConvert.getMetricName());
		ret.append("day", itemToConvert.getDay());
		ret.append("value", itemToConvert.getValue());
		ret.append("valueName", itemToConvert.getValueName());
		return ret;
	}

}
