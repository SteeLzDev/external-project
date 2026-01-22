<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
  <body>
    <script type="text/JavaScript">
      window.opener.document.getElementById('tokenOAuth2').value = '<%= request.getAttribute("OAuth2Token") %>';
      window.opener.document.getElementById('checkOAuth2').style.display = window.opener.document.getElementById('tokenOAuth2').value == '' ? 'none' : 'block';
      window.close();
    </script>
  </body>
</html>
