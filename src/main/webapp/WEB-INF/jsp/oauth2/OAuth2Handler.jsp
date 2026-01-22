<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
  <body>
  <script type="text/JavaScript">
  	var returnJson = '<%=request.getAttribute("json")%>';
  	oauth_eConsig.postMessage(returnJson);
  </script>
  </body>
</html>
