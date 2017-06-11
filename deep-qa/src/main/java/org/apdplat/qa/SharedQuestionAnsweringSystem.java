package org.apdplat.qa;

import java.util.ArrayList;
import java.util.List;

import org.apdplat.qa.datasource.MultiSearchEngineDataSource;
import org.apdplat.qa.files.FilesConfig;
import org.apdplat.qa.datasource.DataSource;
import org.apdplat.qa.datasource.FileDataSource;

import org.apdplat.qa.datasource.BaiduDataSource;
import org.apdplat.qa.system.CommonQuestionAnsweringSystem;
import org.apdplat.qa.system.QuestionAnsweringSystem;

public class SharedQuestionAnsweringSystem {
    private static final QuestionAnsweringSystem QUESTION_ANSWERING_SYSTEM = new CommonQuestionAnsweringSystem();

    static {
        //QUESTION_ANSWERING_SYSTEM.setDataSource(new BaiduDataSource());
        QUESTION_ANSWERING_SYSTEM.setDataSource(new MultiSearchEngineDataSource());
//        List<String> files = new ArrayList<>();
//        files.add(FilesConfig.personNameMaterial);
//        files.add(FilesConfig.locationNameMaterial);
//        files.add(FilesConfig.organizationNameMaterial);
//        files.add(FilesConfig.numberMaterial);
//        files.add(FilesConfig.timeMaterial);
//        files.add(FilesConfig.miscMaterial);
//        QUESTION_ANSWERING_SYSTEM.setDataSource(new FileDataSource(files));
    }

    public static QuestionAnsweringSystem getInstance(){
        return QUESTION_ANSWERING_SYSTEM;
    }

    public static void main(String[] args){
    }
}
