package com.SpringBatchProject.SpringBatchProject.configration;

import com.SpringBatchProject.SpringBatchProject.processor.FileProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


@Configuration
public class SpringBatchConfiguration
{
    @Autowired
   private JobBuilderFactory jbf;

    @Autowired
    private StepBuilderFactory sbf;

    @Autowired
    MongoTemplate mongoTemplate;

    @Bean
    Job job(){

        return jbf.get("file-job-1")
                .incrementer(new RunIdIncrementer())
                .start(step())
                .build();
    }

    @Bean
    private MongoItemReader mongoItemReader(){
        MongoItemReader itemReader=new MongoItemReader();
        itemReader.setCollection("transaction");

        Query query=new Query();
        query.addCriteria(Criteria.where("transaction.merchant.contract.merchantId").in(""));
        itemReader.setQuery(query);
        itemReader.setTemplate(mongoTemplate);
        itemReader.setName("MongoReader");
      //  itemReader.setTargetType();
        return itemReader;
    }

    @Bean
    private ItemProcessor itemProcessor(){



        return  new FileProcessor();
    }

    @Bean
    private ItemWriter itemWriter(){
        return null;
    }

    @Bean
    private Step step() {

        return sbf.get("file-step-1")
                .chunk(1000)
                .reader(mongoItemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }
}
