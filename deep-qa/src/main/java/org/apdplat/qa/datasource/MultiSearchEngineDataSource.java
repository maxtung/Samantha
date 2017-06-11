package org.apdplat.qa.datasource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apdplat.qa.files.FilesConfig;
import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.system.QuestionAnsweringSystem;
import org.apdplat.qa.util.MySQLUtils;
import org.apdplat.qa.util.Tools;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dongbing on 2017/6/11.
 */
public class MultiSearchEngineDataSource  implements DataSource  {

    private static final Logger LOG = LoggerFactory.getLogger(MultiSearchEngineDataSource.class);

    private static final String ACCEPT = "text/html, */*; q=0.01";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String HOST = "www.baidu.com";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:31.0) Gecko/20100101 Firefox/31.0";

    // 获取多少页
    private static final int PAGE = 1;

    private static final int PAGESIZE = 10;

    // 使用摘要
    private static final boolean SUMMARY = true;

    // 使用全文
    //private static final boolean SUMMARY = false;

    //
    private final List<String> files = new ArrayList<>();



    public MultiSearchEngineDataSource() {
        LOG.info("Loading MultiSearchEngineDataSource");
    }

    public MultiSearchEngineDataSource(String file) {
        this.files.add(file);
    }

    public MultiSearchEngineDataSource(List<String> files) {
        this.files.addAll(files);
    }

    @Override
    public Question getQuestion(String questionStr) {
        return getAndAnswerQuestion(questionStr, null);
    }

    @Override
    public List<Question> getQuestions() {
        return getAndAnswerQuestions(null);
    }

    @Override
    public List<Question> getAndAnswerQuestions(QuestionAnsweringSystem questionAnsweringSystem) {
        List<Question> questions = new ArrayList<>();

        for (String file : files) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(file), "utf-8"));
                String line = reader.readLine();
                while (line != null) {
                    if (line.trim().equals("") || line.trim().startsWith("#") || line.indexOf("#") == 1 || line.length() < 3) {
                        //读下一行
                        line = reader.readLine();
                        continue;
                    }
                    LOG.info("从类路径的 " + file + " 中加载Question:" + line.trim());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String questionStr = null;
                    String expectAnswer = null;
                    String[] attrs = line.trim().split("[:|：]");

                    if (attrs == null) {
                        questionStr = line.trim();
                    }
                    if (attrs != null && attrs.length == 1) {
                        questionStr = attrs[0];
                    }
                    if (attrs != null && attrs.length == 2) {
                        questionStr = attrs[0];
                        expectAnswer = attrs[1];
                    }

                    LOG.info("Question:" + questionStr);
                    LOG.info("ExpectAnswer:" + expectAnswer);

                    Question question = getQuestion(questionStr);
                    if (question != null) {
                        question.setExpectAnswer(expectAnswer);
                        questions.add(question);
                    }

                    //回答问题
                    if (questionAnsweringSystem != null && question != null) {
                        questionAnsweringSystem.answerQuestion(question);
                    }

                    //读下一行
                    line = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            LOG.info("从Question文件" + file + "中加载Question，从多个搜索引擎中检索到了 " + questions.size() + " 个Question");
        }
        return questions;
    }

    @Override
    public Question getAndAnswerQuestion(String questionStr, QuestionAnsweringSystem questionAnsweringSystem) {
        //1、先从本地缓存里面找
        Question question = MySQLUtils.getQuestionFromDatabase("multi-se:", questionStr);
        if (question != null) {
            //数据库中存在
            LOG.info("从数据库中查询到Question：" + question.getQuestion());
            //回答问题
            if (questionAnsweringSystem != null) {
                questionAnsweringSystem.answerQuestion(question);
            }
            return question;
        }

        //2、本地缓存里面没有再查询搜索引擎
        question = new Question();
        question.setQuestion(questionStr);

        String query = "";
        try {
            query = URLEncoder.encode(question.getQuestion(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("url构造失败", e);
            return null;
        }

        queryBaidu(question, query, PAGE, PAGESIZE);

        queryGoogle(question, query, PAGE, PAGESIZE);

//        String referer = "http://www.baidu.com/";
//        for (int i = 0; i < PAGE; i++) {
//            query = "http://www.baidu.com/s?tn=monline_5_dg&ie=utf-8&wd=" + query+"&oq="+query+"&usm=3&f=8&bs="+query+"&rsv_bp=1&rsv_sug3=1&rsv_sug4=141&rsv_sug1=1&rsv_sug=1&pn=" + i * PAGESIZE;
//            LOG.debug(query);
//            List<Evidence> evidences = searchBaidu(query, referer);
//            referer = query;
//            if (evidences != null && evidences.size() > 0) {
//                question.addEvidences(evidences);
//            } else {
//                LOG.error("结果页 " + (i + 1) + " 没有搜索到结果");
//                break;
//            }
//        }


//        for (int i = 0; i < PAGE; i++) {
//            query = "http://ajax.googleapis.com/ajax/services/search/web?start=" + i * PAGESIZE + "&rsz=large&v=1.0&q=" + query;
//            List<Evidence> evidences = search(query);
//            if (evidences.size() > 0) {
//                question.addEvidences(evidences);
//            } else {
//                LOG.error("结果页 " + (i + 1) + " 没有搜索到结果");
//                break;
//            }
//        }


        LOG.info("Question：" + question.getQuestion() + " 搜索到Evidence " + question.getEvidences().size() + " 条");
        if (question.getEvidences().isEmpty()) {
            return null;
        }
        //3、将搜索查询结果加入本地缓存
        if (question.getEvidences().size() > 7) {
            LOG.info("将Question：" + question.getQuestion() + " 加入MySQL数据库");
            MySQLUtils.saveQuestionToDatabase("multi-se:", question);
        }

        //回答问题
        if (questionAnsweringSystem != null) {
            questionAnsweringSystem.answerQuestion(question);
        }
        return question;
    }

    private void queryBaidu(Question question, String condition, int page, int pageSize)
    {
        LOG.info("Searching from baidu.");
        String referer = "http://www.baidu.com/";
        for (int i = 0; i < page; i++) {
            String queryUrl = "http://www.baidu.com/s?tn=monline_5_dg&ie=utf-8&wd=" + condition+"&oq="+condition+"&usm=3&f=8&bs="+condition+"&rsv_bp=1&rsv_sug3=1&rsv_sug4=141&rsv_sug1=1&rsv_sug=1&pn=" + i * pageSize;
            LOG.debug(queryUrl);
            List<Evidence> evidences = searchBaidu(queryUrl, referer);
            referer = queryUrl;
            if (evidences != null && evidences.size() > 0) {
                question.addEvidences(evidences);
            } else {
                LOG.error("结果页 " + (i + 1) + " 没有搜索到结果");
                break;
            }
            LOG.info("evidence count from baidu: " + evidences.size());
        }

    }

    private void queryGoogle(Question question, String condition, int page, int pageSize) {
        LOG.info("Searching from Google.");
        for (int i = 0; i < page; i++) {
            String queryUrl = "http://ajax.googleapis.com/ajax/services/search/web?start=" + i * pageSize + "&rsz=large&v=1.0&q=" + condition;
            List<Evidence> evidences = searchGoogle(queryUrl);
            if (evidences.size() > 0) {
                question.addEvidences(evidences);
            } else {
                LOG.error("结果页 " + (i + 1) + " 没有搜索到结果");
                break;
            }

            LOG.info("evidence count from google: " + evidences.size());
        }
    }

    private List<Evidence> searchBaidu(String url, String referer) {
        List<Evidence> evidences = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url)
                    .header("Accept", ACCEPT)
                    .header("Accept-Encoding", ENCODING)
                    .header("Accept-Language", LANGUAGE)
                    .header("Connection", CONNECTION)
                    .header("User-Agent", USER_AGENT)
                    .header("Host", HOST)
                    .header("Referer", referer)
                    .get();
            String resultCssQuery = "html > body > div > div > div > div > div";
            Elements elements = document.select(resultCssQuery);
            for (Element element : elements) {
                Elements subElements = element.select("h3 > a");
                if(subElements.size() != 1){
                    LOG.debug("没有找到标题");
                    continue;
                }
                String title =subElements.get(0).text();
                if (title == null || "".equals(title.trim())) {
                    LOG.debug("标题为空");
                    continue;
                }
                subElements = element.select("div.c-abstract");
                if(subElements.size() != 1){
                    LOG.debug("没有找到摘要");
                    continue;
                }
                String snippet =subElements.get(0).text();
                if (snippet == null || "".equals(snippet.trim())) {
                    LOG.debug("摘要为空");
                    continue;
                }
                Evidence evidence = new Evidence();
                evidence.setSource("BAIDU");
                evidence.setTitle(title);
                evidence.setSnippet(snippet);

                evidences.add(evidence);
            }
        } catch (Exception ex) {
            LOG.error("搜索出错", ex);
        }
        return evidences;
    }

    private List<Evidence> searchGoogle(String query) {
        List<Evidence> evidences = new ArrayList<>();
        try {
            HttpClient httpClient = new HttpClient();
            GetMethod getMethod = new GetMethod(query);

            getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler());

            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                LOG.error("Method failed: " + getMethod.getStatusLine());
            }
            byte[] responseBody = getMethod.getResponseBody();
            String response = new String(responseBody, "UTF-8");
            LOG.info("GOOGLE Response: " + response);

            LOG.debug("搜索返回数据：" + response);
            JSONObject json = new JSONObject(response);
            String totalResult = json.getJSONObject("responseData").getJSONObject("cursor").getString("estimatedResultCount");
            int totalResultCount = Integer.parseInt(totalResult);
            LOG.info("搜索返回记录数： " + totalResultCount);

            JSONArray results = json.getJSONObject("responseData").getJSONArray("results");

            LOG.debug(" Results:");
            for (int i = 0; i < results.length(); i++) {
                Evidence evidence = new Evidence();
                JSONObject result = results.getJSONObject(i);
                String title = result.getString("titleNoFormatting");
                LOG.debug(title);
                evidence.setTitle(title);
                evidence.setSource("GOOGLE");
                if (SUMMARY) {
                    String content = result.get("content").toString();
                    content = content.replaceAll("<b>", "");
                    content = content.replaceAll("</b>", "");
                    content = content.replaceAll("\\.\\.\\.", "");
                    LOG.debug(content);
                    evidence.setSnippet(content);
                } else {
                    //从URL中提取正文
                    String url = result.get("url").toString();
                    String content = Tools.getHTMLContent(url);
                    if (content == null) {
                        content = result.get("content").toString();
                        content = content.replaceAll("<b>", "");
                        content = content.replaceAll("</b>", "");
                        content = content.replaceAll("\\.\\.\\.", "");
                    }
                    evidence.setSnippet(content);
                    LOG.debug(content);
                }
                evidences.add(evidence);
            }
        } catch (Exception e) {
            LOG.error("执行搜索失败：", e);
        }
        return evidences;
    }

    public static void main(String args[]) {
        Question question = new MultiSearchEngineDataSource(FilesConfig.personNameQuestions).getQuestion("APDPlat的创始人是谁？");
        LOG.info(question.toString());
    }
}
