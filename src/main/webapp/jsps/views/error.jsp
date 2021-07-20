<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- Not Used yet DT : 17th June, 2020 -->
<c:set value = "${param.errorInfoParam}" var ="error"  ></c:set>
<c:choose>

	<c:when test="${not empty error}">
	<div class="container">
		<div class="alert alert-danger" role="alert">
			<h4 class="alert-heading">Holy guacamole!</h4>
			<p>We deeply regret to inform you that your Retro tried a lot till last breath, but it couldn't make it.<br/>
			Lets try to operate and diagnose it, Together we can bring it back to Life! <br/>
			You may use below info : <br/><br/>
			<c:forEach var="entry" items="${error}">
									<c:out value="${entry.key}" />
										: <c:out value="${entry.value}" />
										<br/>
								</c:forEach>
			</p>
			
			<hr>
			<p class="mb-0">May the force be with you! If that helps</p>
		</div>
</div>
	</c:when>
	<c:otherwise>
	</c:otherwise>
	</c:choose>
	