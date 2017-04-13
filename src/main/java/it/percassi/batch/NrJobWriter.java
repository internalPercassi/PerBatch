package it.percassi.batch;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import it.percassi.utils.PerPortalConstants;

@Component
public class NrJobWriter implements ItemWriter<String> {

	@Value("${mongoDB.DBname}")
	private String mongoDBName;
	@Value("${mongoDB.URI}")
	private String mongoDBUri;

	private static final Logger LOG = LoggerFactory.getLogger(NrJobWriter.class);



	@Override
	public void write(List<? extends String> itemList) throws JsonProcessingException {
		final MongoClientURI mcu = new MongoClientURI(mongoDBUri);
		final MongoClient mc = new MongoClient(mcu);
		final MongoDatabase mdb = mc.getDatabase(mongoDBName);
		final List<Document> documents = new ArrayList<>();

		
		try {
			
			for (String item : itemList) {
				Document doc = Document.parse(item);
				documents.add(doc);
			}
			LOG.info("Saving data to mongoDB: {}", documents);
			 MongoCollection<Document> collection = mdb.getCollection(PerPortalConstants.NEW_RELIC_COLLECTION);
			collection.insertMany(documents);
		} finally {
			mc.close();
		}

	}

}
