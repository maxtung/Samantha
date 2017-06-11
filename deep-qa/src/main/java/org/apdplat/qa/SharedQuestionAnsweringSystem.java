/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.qa;

import java.util.ArrayList;
import java.util.List;
import org.apdplat.qa.files.FilesConfig;
import org.apdplat.qa.datasource.DataSource;
import org.apdplat.qa.datasource.BaiduDataSource;
import org.apdplat.qa.datasource.FileDataSource;
import org.apdplat.qa.system.CommonQuestionAnsweringSystem;
import org.apdplat.qa.system.QuestionAnsweringSystem;

/**
 * 使用百度数据源的共享问答系统
 * @author 杨尚川
 */
public class SharedQuestionAnsweringSystem {
    //private static final Logger LOG = LoggerFactory.getLogger(SharedQuestionAnsweringSystem.class);

    private static final QuestionAnsweringSystem QUESTION_ANSWERING_SYSTEM = new CommonQuestionAnsweringSystem();
    static{
        //QUESTION_ANSWERING_SYSTEM.setDataSource(new BaiduDataSource());

        //2、问答系统默认文件数据源		
        List<String> files = new ArrayList<>();
        files.add(FilesConfig.personNameMaterial);
        files.add(FilesConfig.locationNameMaterial);
        files.add(FilesConfig.organizationNameMaterial);
        files.add(FilesConfig.numberMaterial);
        files.add(FilesConfig.timeMaterial);
        files.add(FilesConfig.miscMaterial);
        DataSource dataSource = new FileDataSource(files);

        QUESTION_ANSWERING_SYSTEM.setDataSource(dataSource);
        //LOG.info("智能答疑系统调用数据源：" +  QUESTION_ANSWERING_SYSTEM.datasource);
    }
    public static QuestionAnsweringSystem getInstance(){
        return QUESTION_ANSWERING_SYSTEM;
    }
    public static void main(String[] args){

    }
}
