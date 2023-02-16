package com.example.springbach.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person,Person> {
    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);
    @Override
    public Person process(Person item) throws Exception {
        return null;
    }
}
