package be.informatievlaanderen.ldes.server.integration.test.rest.converters;


import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

import static java.util.Optional.ofNullable;
import static org.apache.jena.riot.RDFLanguages.nameToLang;

@Component
public class ModelHttpConverter extends AbstractHttpMessageConverter<Model> {

	public ModelHttpConverter() {
		super(MediaType.ALL);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(Model.class);
	}

	@Override
	protected Model readInternal(Class<? extends Model> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		MediaType contentType = Objects.requireNonNull(inputMessage.getHeaders().getContentType());
		Lang lang =
				ofNullable(nameToLang(contentType.getType() + "/" + contentType.getSubtype()))
						.orElseGet(() -> ofNullable(nameToLang(contentType.getSubtype()))
								.orElseThrow(() -> new RuntimeException("TODO")));
		return RDFParser.source(inputMessage.getBody()).lang(lang).toModel();
	}

	@Override
	protected void writeInternal(Model model, HttpOutputMessage outputMessage)
			throws UnsupportedOperationException, HttpMessageNotWritableException {
		throw new UnsupportedOperationException();
	}
}
