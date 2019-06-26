package com.example.elasticsearch.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "profileDocument")
@Data
public class ProfileDocument {

    private String id;
    private String name;
    private String genders;

}