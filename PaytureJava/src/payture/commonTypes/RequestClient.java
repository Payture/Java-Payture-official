/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payture.commonTypes;

/**
 *
 * @author Soloveva Elena
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
public abstract class RequestClient
    {
        protected HttpClient _client =  HttpClientBuilder.create().build();
        protected String contents;

        /** Method sends data in encoded url
         * @param url - string representation of requested url
         * @param content - UrlEncodedFormEntity with encoded content for request
         * @return response as HttpResponse
        */
        protected HttpResponse post( String url, UrlEncodedFormEntity content )
        {
            try
            {
                HttpPost request = new HttpPost(url);
                request.setEntity(content);
                HttpResponse response =  _client.execute(request);
                //OnResponseReceived( respStr );
                HttpEntity  entity = response.getEntity();
                
                // Read the contents of an entity and return it as a String.
                contents = EntityUtils.toString(entity); //delete this ???
                System.out.println(contents);  //delete this
                return response;
            }
            catch( Exception ex )
            {
                ex.printStackTrace();
                return null;
            }
        }
    }