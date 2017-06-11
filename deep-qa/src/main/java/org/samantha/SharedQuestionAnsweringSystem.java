package org.samantha;

import org.samantha.datasource.MultiSearchEngineDataSource;

import org.samantha.system.CommonQuestionAnsweringSystem;
import org.samantha.system.QuestionAnsweringSystem;

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
