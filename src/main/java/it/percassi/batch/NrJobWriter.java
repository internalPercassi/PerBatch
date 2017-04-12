package it.percassi.batch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import it.percassi.utils.PerPortalConstants;

@StepScope
@Component
public class NrJobWriter implements ItemWriter<String> {

	@Value("${mongoDB.DBname}")
	private String mongoDBName;
	@Value("${mongoDB.URI}")
	private String mongoDBUri;

	// private static final Logger LOG =
	// LoggerFactory.getLogger(NrJobWriter.class);

	@Override
	public void write(List<? extends String> itemList) throws Exception {

		final MongoClientURI mcu = new MongoClientURI(mongoDBUri);
		final MongoClient mc = new MongoClient(mcu);
		final MongoDatabase mdb = mc.getDatabase(mongoDBName);
		final List<Document> documents = new ArrayList<>();

		try {
			for (String item : itemList) {
				Document doc = Document.parse(item);
				documents.add(doc);
			}
			System.out.println("documents to save "
					+ ReflectionToStringBuilder.toString(documents, ToStringStyle.MULTI_LINE_STYLE));
			mdb.getCollection(PerPortalConstants.NEW_RELIC_COLLECTION).insertMany(documents);

		} finally {
			mc.close();
		}

	}

}
