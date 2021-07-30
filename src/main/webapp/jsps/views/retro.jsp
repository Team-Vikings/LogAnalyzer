<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="header.jsp"></jsp:include>


<div class="container">
	<h3>Retro Log Analyzer</h3>

	<jsp:include page="upload.jsp">
		<jsp:param value="analyzeRetroLog" name="action"  />
	</jsp:include>

	<br /> <br />

</div>

<c:choose>

	<c:when test="${not empty validateMap}">
		<div class="container">
		 <div class="alert alert-danger" role="alert">
			<h4 class="alert-heading">Ughh!!! Looks like you uploaded incorrect log, Didn't you? &#128530;</h4>
			<br/>
			<p>Below is all it takes to keep me happy : &#128539;<br/>
			 <br/>
			Logging Category : "GMFZT"  <br/>
			Thread size : "1" <br/>
			Person Count : "1" <br/>
			Action Type : "L" <br/>
			<br/> 
			<br/>
			While you have used - <c:forEach var="entry" items="${validateMap}">
									<c:out value="${entry.key}" />
										: <c:out value="${entry.value}" />
										,
								</c:forEach>
			</p>
			
			<hr>
			<p class="mb-0">Nevermind! I'hv got your back! Come back again with correct log! &#128519;</p>
		</div> 
		</div>
	</c:when>
	<c:when test="${not empty errorInfo}">
	
	<div class="container">
	 <div class="alert alert-danger" role="alert">
			<h4 class="alert-heading">Holy guacamole! &#128561; </h4>
			<p>We deeply regret to inform you that your Retro tried a lot till last breath, but it couldn't make it.<br/>
			Lets try to operate and diagnose it, together we can bring it back to Life! <br/>
			You may use below info : <br/><br/>
			<c:forEach var="entry" items="${errorInfo}">
									<c:out value="${entry.key}" />
										: <c:out value="${entry.value}" />
										<br/>
								</c:forEach>
			</p>
			
			<hr>
			<p class="mb-0">May the force be with you! (If that helps) &#128556;</p>
		</div> 
		
</div>
	</c:when>
	<c:otherwise>

		<c:if test="${not empty retroInfo }">
			 <div class="container"><div class="alert alert-success" role="alert">
  			Cool, A perfect log to analyze! Below is what we have for you!
			</div>
				<div class="row">
					<div class="col-sm">

						<div class="card" style="min-height: 400px;">
							<div class="card-header">
								<h5>Basic Information on this Retro</h5>
							</div>

							<ul class="list-group list-group-flush">
								<c:forEach var="entry" items="${retroInfo}">
									<li class="list-group-item"><c:out value="${entry.key}" />
										: <c:out value="${entry.value}" /></li>
								</c:forEach>
							</ul>
						</div>


					</div>
					<div class="col-sm">

						<div class="card" style="min-height: 400px;">
							<div class="card-header"><h5>Run Result Differences</h5></div>
							<div class="card-body">
								<!-- <h5 class="card-title">Special title treatment</h5>  -->
								<c:if
									test="${not empty originalValFileName and not empty newValFileName }">
									<p class="card-text">
										Download the Original Run Result and New Run Results files separately from below links :</p>
										<div class="card-body">
										<a href="download?fileName=${originalValFileWithOutPath}"
											class="card-link">Original RunResults</a> <a
											href="download?fileName=${newValFileWithOutPath}"
											class="card-link"> New RunResults</a>
									</div>
    - RR present in Original Run Result but not in New Run Result means EE was Deleted!
    <br />
								
    - RR not present in Original Run result but present in New Run Result means EE was Inserted!
    <br /><br/>
								
									<c:if test="${not empty retroDiagFileName}">
										<div style="align-items: center;" class="card-body">
										<a href="download?fileName=${retroDiagFileName}"
											class="card-link">Download Retro Diagnostic File Here</a>
									</div>
									</c:if>

									<%-- <%
								String originalFile = (String) request.getAttribute("originalValFileName");
							String newFile = (String) request.getAttribute("newValFileName");
							Runtime runtime = Runtime.getRuntime();
							// Process exec = runtime.exec(new String[]{"java", "-cp", "/home/test/test.jar", "Main"});
							Process process = runtime.exec("/usr/local/bin/bcomp " + originalFile + " " + newFile);
							%>
  --%>
								</c:if>
							</div>
						</div>

						<!--<c:if
					test="${not empty originalValFileName and not empty newValFileName }">
 The Difference of Original Run Result and New Run Result has been opened in Beyond Comparator
 Original Run Results : - <a
						href="download?fileName=${originalValFileName}">Original
						Values</a>
 New Run Results : - <a href="download?fileName=${newValFileName}">New
						Values</a>


				</c:if>
-->
					</div>
					<div class="col-sm">

						<div class="card">
							<div class="card-header">
								<h5>List of Payroll Actions reprocessed</h5>
							</div>
							<div class="scrollable" style="max-height: 345px;overflow-y: auto;">
						
							<c:forEach items="${redoActionList}" var="map">
								<ul class="list-group list-group-flush">
									<li class="list-group-item"><c:forEach items="${map}"
											var="entry">
											<c:out value="${entry.key}" />
									: <c:out value="${entry.value}" />
											<br />
										</c:forEach></li>
								</ul>
							</c:forEach>
							</div>
						</div>
					</div>
				</div>
</div>
				</br>
				<div class="row">
				
					<div class="col">
					<c:choose >
					<c:when test="${not empty retroEntryList}">
					<div class="card-header">
								<h5>List of Generated Retro Entries and their Sources</h5>
							</div>
						<table class="table table-bordered">
							<thead class="thead-light">
								<tr>
									<th data-toggle="tooltip" data-placement="top" title="Element Type of Retro" scope="col">RETRO_ELE_TYPE_ID</th>
									<th data-toggle="tooltip" data-placement="top" title="Entry Type" scope="col">ENTRY_TYPE</th>
									<th data-toggle="tooltip" data-placement="top" title="EE - Generated through EE, RR- Generated through change in RR" scope="col">CREATOR_TYPE</th>
									<th data-toggle="tooltip" data-placement="top" title="Payroll Action Id where it was originally processed" scope="col">SOURCE_ACTION_ID</th>
									<th data-toggle="tooltip" data-placement="top" title="Source Entry which triggered that entry" scope="col">SOURCE_ENTRY_ID</th>
									<th data-toggle="tooltip" data-placement="top" title="EE generated by Retro - PK" scope="col">RETRO_ENTRY_ID</th>
									<th data-toggle="tooltip" data-placement="top" title="RR of New Run" scope="col">RETRO_RUN_RESULT_ID</th>
									<th data-toggle="tooltip" data-placement="top" title="RR of Original Run" scope="col">SOURCE_RUN_RESULT_ID</th>
									
								</tr>
							</thead>


							<tbody>

								<c:forEach items="${retroEntryList}" var="map">
									<tr>

										<c:forEach items="${map}" var="entry">
											<td>${entry.value}</td>
										</c:forEach>

									</tr>
								</c:forEach>
								<!--<c:forEach items="${retroEntryList}" var="map">
									<tr>

										<td><c:set var="myParam" value="retro_ele"/>
										<c:if test ="${ myParam eq map.key }">
whatEver: <c:out value="${map.value}"/></c:if></td>

									</tr>
								</c:forEach>-->
								
							</tbody>




						</table>
						<div class="card-header">
								<p>General Info : <br/>
								Blank RETRO_RUN_RESULT_ID denotes that entry was not processed in New Retro's Run.(-ve Entry)<br/>
								Blank SOURCE_RUN_RESULT_ID denotes that entry was not processed in Original Run.(+ve Entry) 
								
</p>
							</div>
						</c:when>
						<c:otherwise>
						 <div class="container"><div class="alert alert-warning" role="alert">
  						Oops! Looks like there were no Retro Entries generated!
						</div></div>
						</c:otherwise>
						</c:choose>
					</div>
				</div>
			
		</c:if>

	</c:otherwise>

</c:choose>


</body>
</html>
