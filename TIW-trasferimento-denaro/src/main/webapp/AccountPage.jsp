<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Stato del conto</title>
<link rel="stylesheet" type="text/css"  href="css/common.css">
<link rel="stylesheet" type="text/css"  href="css/account.css">
</head>
<body>
	<c:url value="/Homepage" var="homeUrl" />
	<a class="button" href="${homeUrl}">Indietro</a>
	<h1>Conto corrente n. <c:out value="${account.id}"/></h1>
	<h2>Saldo $<c:out value="${account.balance}"/></h2>
	<div id="form-wrap">
	<h2>Nuovo trasferimento</h2>
	<c:url value="/CreateTransfer" var="createUrl" />
	<form method="POST" action="${createUrl}">
		<p><input type="number" name="amount" placeholder="Importo"/></p>
		<p><input type="text" name="reason" placeholder="Causale"/></p>
		<p><input type="text" name="username" placeholder="Codice utente destinatario"/></p>
		<p><input type="text" name="accountId" placeholder="Codice conto destinatario"/></p>
		<p><input type="submit" value="INVIA" /></p>
		<input type="hidden" name="fromAccount" value="${account.id}" hidden="true" />
	</form>
	</div>
	<h2>Lista trasferimenti</h2>
	<c:choose>
	<c:when test="${transfers.size() > 0}">
	<table>
	<thead>
		<tr>
			<td><b>Importo</b></td>
			<td><b>Causale</b></td>
			<td><b>Origine</b></td>
			<td><b>Destinatario</b></td>
			<td><b>Data</b></td>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="transfer" items="${transfers}">
			<tr>
				<td>$<c:out value="${transfer.amount}"/></td>
				<td><c:out value="${transfer.reason}"/></td>
				<td>Conto n. <c:out value="${transfer.fromAccount}"/></td>
				<td>Conto n. <c:out value="${transfer.toAccount}"/></td>
				<td><c:out value="${transfer.date.toString()}"/></td>
			</tr>
		</c:forEach>
	</tbody>
	</table>
	</c:when>
	<c:otherwise>
		Esegui un trasferimento usando l'apposita form
	</c:otherwise>
	</c:choose>
</body>
</html>