package be.informatievlaanderen.ldes.server.integration.test.rest.converters;


import be.informatievlaanderen.ldes.server.integration.test.rest.dtos.MemberDTO;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

import static java.util.Optional.ofNullable;
import static org.apache.jena.riot.RDFLanguages.nameToLang;

@Component
public class MemberHttpConverter extends AbstractHttpMessageConverter<MemberDTO> {

	public MemberHttpConverter() {
		super(MediaType.ALL);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(MemberDTO.class);
	}

	@Override
	protected MemberDTO readInternal(Class<? extends MemberDTO> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		MediaType contentType = Objects.requireNonNull(inputMessage.getHeaders().getContentType());
		Lang lang =
				ofNullable(nameToLang(contentType.getType() + "/" + contentType.getSubtype()))
						.orElseGet(() -> ofNullable(nameToLang(contentType.getSubtype()))
								.orElseThrow(() -> new RuntimeException("TODO")));
		Model memberModel = RDFParser.source(inputMessage.getBody()).lang(lang).toModel();

		return new MemberDTO(memberModel);
	}

	@Override
	protected void writeInternal(MemberDTO memberDTO, HttpOutputMessage outputMessage)
			throws UnsupportedOperationException, HttpMessageNotWritableException {
		throw new UnsupportedOperationException();
	}
}
