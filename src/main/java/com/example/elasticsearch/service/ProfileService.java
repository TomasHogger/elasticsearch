package com.example.elasticsearch.service;


import com.example.elasticsearch.model.ProfileDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.similarity.ScriptedSimilarity;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static org.elasticsearch.action.update.UpdateHelper.ContextFields.INDEX;
import static org.elasticsearch.cluster.SnapshotsInProgress.TYPE;


@Service
@Slf4j
public class ProfileService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ObjectMapper objectMapper;

    public Terms searchTerm() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("by_name").field("name.keyword");
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);

        Terms terms = searchResponse.getAggregations().get("by_name");
        return terms;
    }

    public String createProfile(ProfileDocument document) throws Exception {

        UUID uuid = UUID.randomUUID();
        document.setId(uuid.toString());

        Map<String, Object> documentMapper = objectMapper.convertValue(document, Map.class);

        IndexRequest indexRequest = new IndexRequest("index", "_doc", document.getId()).source(documentMapper);

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);

        return indexResponse
                .getResult()
                .name();
    }


    public ProfileDocument findById(String id) throws Exception {

        GetRequest getRequest = new GetRequest("index",id);

        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        Map<String, Object> resultMap = getResponse.getSource();

        return objectMapper
                .convertValue(resultMap, ProfileDocument.class);
    }


    public String updateProfile(ProfileDocument document) throws Exception {

        ProfileDocument resultDocument = findById(document.getId());

        UpdateRequest updateRequest = new UpdateRequest(
                INDEX,
                TYPE,
                resultDocument.getId());

        Map<String, Object> documentMapper =
                objectMapper.convertValue(document, Map.class);

        updateRequest.doc(documentMapper);

        UpdateResponse updateResponse =
                client.update(updateRequest, RequestOptions.DEFAULT);

        return updateResponse
                .getResult()
                .name();

    }


    public List<ProfileDocument> findAll() throws Exception {

        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);


    }

    private List<ProfileDocument> getSearchResult(SearchResponse response) {

        SearchHit[] searchHit = response.getHits().getHits();

        List<ProfileDocument> profileDocuments = new ArrayList<>();

        if (searchHit.length > 0) {

            Arrays.stream(searchHit)
                    .forEach(hit -> profileDocuments
                            .add(objectMapper
                                    .convertValue(hit.getSourceAsMap(),
                                            ProfileDocument.class))
                    );
        }

        return profileDocuments;
    }


    public String deleteProfileDocument(String id) throws Exception {

        DeleteRequest deleteRequest = new DeleteRequest(id);
        DeleteResponse response =
                client.delete(deleteRequest, RequestOptions.DEFAULT);

        return response
                .getResult()
                .name();

    }


}
