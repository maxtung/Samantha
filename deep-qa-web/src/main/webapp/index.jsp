<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.samantha.model.Question"%>
<%@page import="org.samantha.model.Evidence"%>
<%@page import="org.samantha.model.CandidateAnswer"%>
<%@page import="org.samantha.model.QuestionType"%>
<%@page import="org.samantha.SharedQuestionAnsweringSystem"%>
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
    <meta http-equiv="Content-Type" ; content="text/html" ; charset=UTF-8 ">

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
            <div class="col-md-8">
                <form>
                    <div class="form-group">
                        <input type="text" class="form-control" id="q" name="q" placeholder="请输入您的问题">
                        <br/> 
                        
                        <a class="btn btn-info" href="#" onclick="answer();">手气不错</a>
                    </div>
                </form>
            </div>
        </div>
      

    </div>

</body>
</html>
