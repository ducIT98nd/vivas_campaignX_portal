package com.vivas.campaignx.common;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class HttpUtil {

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    public String PostWithJSON(String url, String json){
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = null;
        try {
            entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse response = client.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            client.close();
            return responseBody;
        } catch (UnsupportedEncodingException e) {
            logger.error("error when post to clickhouse adapter: ", e);
            return null;
        }catch (Exception e){
            logger.error("error when post to clickhouse adapter: ", e);
            return null;
        }
    }

    public String genQuery(String url, String jsonMainGroup) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonMainGroup);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse response = client.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            logger.info("sucess when post to clickhouse adapter:", responseBody);
            client.close();
            return responseBody;
        } catch (UnsupportedEncodingException e) {
            logger.error("error when post to clickhouse adapter: ", e);
            return null;
        }catch (Exception e){
            logger.error("error when post to clickhouse adapter: ", e);
            return null;
        }
    }

    public String postWithParamJson( String url, String json) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addTextBody("jsonData", json, ContentType.TEXT_PLAIN);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            client.close();
            return responseBody;
        } catch (UnsupportedEncodingException e) {
            logger.debug("error when post to clickhouse adapter: ", e);
            return null;
        }catch (Exception e){
            logger.debug("error when post to clickhouse adapter: ", e);
            return null;
        }
    }

    public String postWithJsonAndFile( String url, String json, String pathFile) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        try {
            File file = new File(pathFile);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody
                    ("file", file, ContentType.DEFAULT_BINARY, pathFile);
            builder.addTextBody("jsonData", json, ContentType.TEXT_PLAIN);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            client.close();
            return responseBody;
        } catch (UnsupportedEncodingException e) {
            logger.debug("error when post to clickhouse adapter: ", e);
            return null;
        }catch (Exception e){
            logger.debug("error when post to clickhouse adapter: ", e);
            return null;
        }
    }

    public String postWithFileAndEventCampaignId( String url, String pathFile, Long eventCampaignId) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        try {
            File file = new File(pathFile);
            logger.info("file {}", file);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody
                    ("fileCustomer", file, ContentType.DEFAULT_BINARY, pathFile);
            builder.addTextBody("eventCampaignId", eventCampaignId.toString(), ContentType.TEXT_PLAIN);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            client.close();
            return responseBody;
        } catch (UnsupportedEncodingException e) {
            logger.debug("error when post to clickhouse adapter: ", e);
            return null;
        }catch (Exception e){
            logger.debug("error when post to clickhouse adapter: ", e);
            return null;
        }
    }

    public String postWithEntity(String url, HttpEntity entity) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            client.close();
            return responseBody;
        } catch (UnsupportedEncodingException e) {
            logger.debug("error when post to clickhouse adapter: ", e);
            return null;
        }catch (Exception e){
            logger.debug("error when post to clickhouse adapter: ", e);
            return null;
        }
    }

    public String postWithJsonAndFileAndFileBlacklist( String url, String json, String pathFile, String pathFileBlacklist) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        try {
            File file = null;
            File fileBlacklist = null;
            if(pathFile != null) file = new File(pathFile);
            if(pathFileBlacklist != null) fileBlacklist = new File(pathFileBlacklist);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, pathFile);
            builder.addBinaryBody("fileBlacklist", fileBlacklist, ContentType.DEFAULT_BINARY, pathFileBlacklist);
            builder.addTextBody("jsonData", json, ContentType.TEXT_PLAIN);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            client.close();
            return responseBody;
        } catch (UnsupportedEncodingException e) {
            logger.debug("error when post to clickhouse adapter: ", e);
            return null;
        }catch (Exception e){
            logger.debug("error when post to clickhouse adapter: ", e);
            return null;
        }
    }

    public String Get(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        String responseBody = null;
        try {
            HttpResponse httpresponse = httpClient.execute(httpGet);
            responseBody = EntityUtils.toString(httpresponse.getEntity(), StandardCharsets.UTF_8);
        }catch (Exception e){
            logger.error("error: ", e);
        }
        return responseBody;
    }

}
