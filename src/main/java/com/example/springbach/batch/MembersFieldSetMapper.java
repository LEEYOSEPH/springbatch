package com.example.springbach.batch;

import com.example.springbach.entity.Members;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class MembersFieldSetMapper implements FieldSetMapper<Members> {
    @Override
    public Members mapFieldSet(FieldSet fieldSet) throws BindException {
        return   Members.builder()
                .memberName(fieldSet.readRawString(0))
                .age(Integer.valueOf(fieldSet.readRawString(1)))
                .birthDay(fieldSet.readRawString(2))
                .build();
    }
}
