<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstL/core" %>
  <!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>display dataset</title>

<style type="text/css">
              h1{color:blue;}
              th{background-color :orange ; color:white;}
              a{text-decoration :none;}
</style>
</head>
<body>
<center>
    <%= "Hello World!" %>
    <h1>display student record </h1>
    <h4>${msg} </h4>
    <table border="4" width="40%">
    <tr>
     <th>Repo Name </th>
     <th>Readme exist</th>
    </tr>
      <c:forEach var="tab" items="${data }">
    <tr>
        <td>${tab.reponame}</td>
         <td>${tab.readmeexist}</td>
     </tr>
     </c:forEach>
    </table>

    <a href="download/Repos">Click Here...</a>
    </center>
</body>
</html>