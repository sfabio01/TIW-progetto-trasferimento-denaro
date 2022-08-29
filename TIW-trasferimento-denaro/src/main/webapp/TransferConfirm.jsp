<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Success</title>
<link rel="stylesheet" type="text/css"  href="css/common.css">
</head>
<body>
	<h1>Conferma trasferimento</h1>
	<p>Il tuo trasferimento è stato processato correttamente</p>
	<h2>Riepilogo</h2>
	<p>Importo: $<c:out value="${summary.transfer.getAmount()}" /></p>
	<p>Data: <c:out value="${summary.transfer.getDate().toString()}" /></p>
	<h3>Conto corrente n. <c:out value="${summary.fromAccountOld.getId()}" /> (origine)</h3>
	<p>Saldo precedente: $<c:out value="${summary.fromAccountOld.getBalance()}" /></p>
	<p>Saldo attuale: $<c:out value="${summary.fromAccountNew.getBalance()}" /></p>
	<h3>Conto corrente n. <c:out value="${summary.toAccountOld.getId()}" /> (destinatario)</h3>
	<p>Saldo precedente: $<c:out value="${summary.toAccountOld.getBalance()}" /></p>
	<p>Saldo attuale: $<c:out value="${summary.toAccountNew.getBalance()}" /></p>
	<c:url var="url" value="/Homepage"/>
	<p><a class="button" href="${url}">Ok</a></p>
</body>
</html>