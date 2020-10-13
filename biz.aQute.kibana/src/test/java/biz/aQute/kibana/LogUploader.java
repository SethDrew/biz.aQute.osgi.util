package biz.aQute.kibana;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import aQute.lib.io.IO;

@Designate(ocd=KibanaLogUploader.Configuration.class, factory = false)
@Component
public class LogUploader {

	@ObjectClassDefinition
	public @interface Configuration {
		String[] hosts();
		String password();
	}
	
	CredentialsProvider credentialsProvider =
		    new BasicCredentialsProvider();
		
		
	RestClientBuilder builder = RestClient.builder(
		    new HttpHost("16457eb61dd34826b6463db0c8aae5f8.us-east-1.aws.found.io", 9243, "https"))
			.setHttpClientConfigCallback(new HttpClientConfigCallback() {
		        @Override
		        public HttpAsyncClientBuilder customizeHttpClient(
		                HttpAsyncClientBuilder httpClientBuilder) {
		            return httpClientBuilder
		                .setDefaultCredentialsProvider(credentialsProvider);
		        }
		    });

	RestClient client;

	@Before
    public void setup() {
		credentialsProvider.setCredentials(AuthScope.ANY,
			    new UsernamePasswordCredentials("elastic", "di17DinEhnHERcm8FqeVYZXx"));
		 client = builder.build();
	}

	@Test
	public void post() throws IOException {
		Request request = new Request( "POST", "/logs-foo/_doc");
		request.setJsonEntity("{ \"l\": [ {\"date\":"+System.currentTimeMillis()+",\"thread\":\"[t8]\",\"level\":\"DEBUG\",\"logger\":\"LOGGER\",\"message\":\"msg\"},"+"{\"date\":"+System.currentTimeMillis()+",\"thread\":\"[t1]\",\"level\":\"DEBUG\",\"logger\":\"LOGGER\",\"message\":\"msg\"}]}");
		Response response = client.performRequest(request);
		System.out.println(response);
	}
	
	@Test
	public void bulk() throws IOException {
		Request request = new Request( "POST", "/logs-foo/_bulk");
		request.setJsonEntity(
				"{ \"index\": {}}\n" + //
		        "{\"date\":"+System.currentTimeMillis()+",\"thread\":\"[t5]\",\"level\":\"DEBUG\",\"logger\":\"LOGGER\",\"message\":\"xxxxxxxxxxxxxxxxxxxxxxxxxxx\"}\n" //
				+"{ \"index\": {}}\n" + //
		        "{\"date\":"+System.currentTimeMillis()+",\"thread\":\"[t3]\",\"level\":\"DEBUG\",\"logger\":\"LOGGER\",\"message\":\"yyyyyyyyyyyyyyyyyyyyyyyyyyy\"}\n");
		Response response = client.performRequest(request);
		System.out.println(response);
	}
	

	@Test
	public void get() throws IOException {
		Request request = new Request( "GET", "/logs-foo/_doc/dQZh_nQBKVTtM-kte3_1");
		Response response = client.performRequest(request);
		String collect = IO.collect(response.getEntity().getContent());
		System.out.println(collect);
	}

	@Test
	public void search() throws IOException {
		Request request = new Request( "GET", "/logs-foo/_search");
		Response response = client.performRequest(request);
		String collect = IO.collect(response.getEntity().getContent());
		System.out.println(collect);
	}
}
