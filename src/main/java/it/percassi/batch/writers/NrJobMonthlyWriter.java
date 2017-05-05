package it.percassi.batch.writers;

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

import it.percassi.batch.mongo.NewRelicMongoMonthlyItem;
import it.percassi.utils.PerPortalConstants;

public class NrJobMonthlyWriter implements ItemWriter<NewRelicMongoMonthlyItem> {

	@Value("${mongoDB.DBname}")
	private String mongoDBName;
	@Value("${mongoDB.URI}")
	private String mongoDBUri;

	private static final Logger LOG = LoggerFactory.getLogger(NrJobMonthlyWriter.class);

	@Override
	public void write(List<? extends NewRelicMongoMonthlyItem> item) throws JsonProcessingException, MongoException {

		final MongoClientURI mcu = new MongoClientURI(mongoDBUri);
		final MongoClient mc = new MongoClient(mcu);
		final MongoDatabase mdb = mc.getDatabase(mongoDBName);
		Document doc = null;

		try {
			
			doc = toBSONDoc(item.get(0));
			
			MongoCollection<Document> collection = mdb.getCollection(PerPortalConstants.NEW_RELIC_COLLECTION_MONTHLY);
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

	private Document toBSONDoc(NewRelicMongoMonthlyItem itemToConvert) {
		Document ret = new Document();
		ret.append("metricName", itemToConvert.getMetricName());
		ret.append("day",null);
		ret.append("value", itemToConvert.getValue());
		ret.append("valueName", itemToConvert.getValueName());
		ret.append("yearMonth", itemToConvert.getYearMonth());
		return ret;
	}

}
