package it.percassi.batch;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import it.percassi.utils.PerPortalConstants;

@Component
public class NrJobWriter implements ItemWriter<List<NewRelicMongoItem>> {

	@Value("${mongoDB.DBname}")
	private String mongoDBName;
	@Value("${mongoDB.URI}")
	private String mongoDBUri;

	@Override
	public void write(List<? extends List<NewRelicMongoItem>> itemsToStore) throws Exception {

		final MongoClientURI mcu = new MongoClientURI(mongoDBUri);
		final MongoClient mc = new MongoClient(mcu);
		final MongoDatabase mdb = mc.getDatabase(mongoDBName);

		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			final String itemAsString = objectMapper.writeValueAsString(itemsToStore);
			Document doc = Document.parse(itemAsString);
			mdb.getCollection(PerPortalConstants.NEW_RELIC_COLLECTION).insertMany(Arrays.asList(doc));
		} finally {
			mc.close();

		}
	}

}

// @Override
// public void write(List<? extends NewRelicMongoItem> itemToSave) throws
// Exception {
//

//
// }
