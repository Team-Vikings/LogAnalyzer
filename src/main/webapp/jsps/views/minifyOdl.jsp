<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <jsp:include page="header.jsp"></jsp:include>
    <div class="container">
        <h3>Minify Odl</h3>
        <jsp:include page="upload.jsp">
            <jsp:param value="minifyOdl" name="action" />
        </jsp:include>
        
        <br>
        <c:if test="${not empty filePath}">
        <div class="card card-body w-78 p-3 "> 
        <br>
            Your file has been successfully processed and you can download it from below

            Download Processed File
            <a href="download?fileName=${filePath}">${filePath}</a>
        </c:if>
        <c:if test="${not empty incError}">
            <br>
            <br>
            <div class="container">
                <button class="btn btn-info btn-block" type="button" data-toggle="collapse" data-target="#incidentsreported">
                    Show all incidents reported in file
                </button>
                <div id="incidentsreported" class="collapse">
                <div class="card card-body">
                    <c:forEach items="${incError}" var="incErr">
                        Incident ${incErr.key} more details in ${incErr.value}
                        <br>
                    </c:forEach>
                    </div>
                </div>
            </div>
        </c:if>
        <br>
   
        <c:if test="${not empty exceptionStack}">
        <div class="container">
            <button class="btn btn-primary btn-block" type="button" data-toggle="collapse" data-target="#stacktrace">
                Stack Traces in File
            </button>
                <div id="stacktrace" class="collapse">             
                <div class="card card-body">
                <c:forEach items="${exceptionStack}" var="excep">
                <div class="card card-body">
                    ${excep}
                    </div>
                    <br>
                </c:forEach>
                </div>
                </div>
        </div>
        </c:if>
        


        <br>
     


        <c:if test="${not empty lstError}">
            <div class="container">
                <button type="button" class="btn btn-danger btn-block" data-toggle="collapse" data-target="#demo">View All
                    Errors</button>
                <div id="demo" class="collapse">
                <div class="card card-body">
                    <c:forEach items="${lstError}" var="err">
                    <div class="card card-body">
                        ${err}
                        </div>
                        <br>
                    </c:forEach>
                </div>
                </div>
            </div>
        </c:if>
   
    </div>
    </div>
    </body>

    </html>