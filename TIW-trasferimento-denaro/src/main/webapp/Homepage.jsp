<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Homepage</title>
<link rel="stylesheet" type="text/css"  href="css/common.css">
<link rel="stylesheet" type="text/css"  href="css/homepage.css">
</head>
<body>
	<h1>Ciao, <c:out value="${user.getFirstname()}"/> <c:out value="${user.getLastname()}"/></h1>
	<h2>I tuoi conti corrente</h2>
	<c:choose>
		<c:when test="${accounts.size()>0}">
			<table>
				<tbody>
					<c:forEach var="account" items="${accounts}">
						<tr>
							<c:url var="accountUrl" value="/AccountPage?id=${account}"/>
							<td><a href="${accountUrl}">Conto corrente n. <c:out value="${account}"/></a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:otherwise>
			No accounts to view
		</c:otherwise>
	</c:choose>
	<p><a class="button" href="<c:url value="Logout"/>">Logout</a></p>
</body>
</html>