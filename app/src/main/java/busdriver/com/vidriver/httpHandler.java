package busdriver.com.vidriver;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Richard on 01/07/2015.
 */
public class httpHandler {
    public String post2(String posturl, String email, String Pass){
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(posturl);
            //Añadir Parametros
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username",email));
            params.add(new BasicNameValuePair("password",Pass));
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String responseText = EntityUtils.toString(entity);
            return responseText;
        }
        catch(Exception e) { return e.getMessage(); }
    }
    public String post(String posturl){
        try{
            posturl = posturl.trim();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(posturl);
            //Añadir Parametros
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email","richardalozada@hotmail.com"));
            params.add(new BasicNameValuePair("pass","54548asdsad"));
            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String responseText = EntityUtils.toString(entity);
            return responseText;
        }
        catch(Exception e) { return e.getMessage(); }
    }
    public String post3(String posturl){
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(posturl);
            //Añadir Parametros
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            httppost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String responseText = EntityUtils.toString(entity);
            return responseText;
        }
        catch(Exception e) { return e.getMessage(); }
    }
}

