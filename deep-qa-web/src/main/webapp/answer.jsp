   <%@page contentType="text/html" pageEncoding="UTF-8"%>    
   <%@page import="org.samantha.model.Question"%>
   <%@page import="org.samantha.model.Evidence"%>
   <%@page import="org.samantha.model.CandidateAnswer"%>
   <%@page import="org.samantha.model.QuestionType"%>
   <%@page import="org.samantha.SharedQuestionAnsweringSystem"%>
   <%@page import="org.samantha.parser.WordParser"%>
   <%@page import="java.util.List"%>
   <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
    <%
       request.setCharacterEncoding("UTF-8");
       String questionStr = request.getParameter("q");
       Question question = null;
       List<CandidateAnswer> candidateAnswers = null;
       if (questionStr != null && questionStr.trim().length() > 3) {
           question = SharedQuestionAnsweringSystem.getInstance().answerQuestion(questionStr);
           if (question != null) {
            candidateAnswers = question.getAllCandidateAnswer();
           }
       }
    %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js "></script>

    <!-- 最新版本的 Bootstrap 核心 CSS 文件 -->
    <link rel="stylesheet " href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css " integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u " crossorigin="anonymous ">

    <!-- 可选的 Bootstrap 主题文件（一般不用引入） -->
    <link rel="stylesheet " href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap-theme.min.css " integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp " crossorigin="anonymous ">

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js " integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa " crossorigin="anonymous "></script>

    <script type="text/javascript " src="./js/deep-qa.js "></script>

    <title>智能答疑系统演示</title>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-4">
                <h1><p class="text-info">智能答疑系统演示</p></h1>
            </div>
        </div>
        <br/> 

        <div class="row">
            <div class="col-md-4">
                <h3><p class="text-warning">问题分析</p></h3>
            </div>
        </div>
 
<%
    if (question != null) {
%>        
        <div class="row" style="padding: 10px">
            <div class="col-md-8">

                <h4>
                    <p class="text-muted"><font>问题: <%=question.getQuestion()%></font></p>
                </h4>

                <h4>
                    <p class="text-muted"><font>问题类型：<%=question.getQuestionType().getDes()%>/<%=question.getQuestionType().getPos()%></font>
                    </p>
                </h4>
                
                <h4>
                    <p class="text-muted"><font>问题关键词: <%=WordParser.parse(question.getQuestion().replace("?", "").replace("？", ""))%></font> 
                    </p>
                </h4>
            </div> 
        </div> 
        <br/>

        <div class="row">
            <div class="col-md-4">
                <h3><p class="text-warning">答案分析</p></h3>
            </div>
        </div>

<%
if (candidateAnswers != null && candidateAnswers.size() > 0) {
%>   
<div style="padding: 10px">
<table class="table table-condensed" >
    <tr><th>序号</th><th>候选答案</th><th>答案评分</th></tr>
    <%
    int i = 0;
    for (CandidateAnswer candidateAnswer : candidateAnswers) {
    if ((++i) == 1) {
    %>          
    <tr><td><font color="red"><%=i%></font></td><td><font color="red"><%=candidateAnswer.getAnswer()%></font></td><td><font color="red"><%=candidateAnswer.getScore()%></font></td></tr>
<%
} else {
%>
    <tr><td><%=i%></td><td><%=candidateAnswer.getAnswer()%></td><td><%=candidateAnswer.getScore()%></td></tr>
<%
}
%>

<%
}
%> 
</div>       
</table>
<%
}
%>

        <div class="row">
            <div class="col-md-4">
                <h3><p class="text-warning">证据分析</p></h3>
            </div>
        </div>
        <div style="padding: 10px">
<%
int j = 1;
for (Evidence evidence : question.getEvidences()) {
%>
    <div class="row">
        <div class="col-md-1">
            <p class="text-primary"> 页面 <%=j%> : </p>
        </div>
        <div class="col-md-11">
            <p class="text-muted"><%=evidence.getSource() + ": " + evidence.getTitle()%></p>
        </div>
    </div>

    <div class="row">
        <div class="col-md-1">
            <p class="text-primary"> 信息 <%=j%> : </p>
        </div>
        <div class="col-md-11">
            <p class="text-muted"><%=evidence.getSnippet()%></p>
        </div>
    </div>

    <div class="row">
        <div class="col-md-1">
            <p class="text-primary"> 分值 <%=j%> : </p>
        </div>
        <div class="col-md-11">
            <p class="text-muted"><%=evidence.getScore()%></p>
        </div>
    </div>

    <!--
    <div class="row">
        <div class="col-md-1">
            <p class="text-primary"> 关键词 <%=j%> : </p>
        </div>
        <div class="col-md-11">
            <p class="text-muted"><%=WordParser.parse(evidence.getSnippet())%></p>
        </div>
    </div>
    -->
    <br/>
<%
j++;
}
%>
</div>

<%
} 

else {
%>
<%
}
%>      
<br/>

<button class="btn btn-info" onclick="window.location='index.jsp';">返回主页</button>
<br/><br/><br/>
    </div>    



</body>
</html>