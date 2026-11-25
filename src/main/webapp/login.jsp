<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login - LibraryHub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        body {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
            padding: 20px;
        }
        .login-card {
            background-color: #ffffff;
            border-radius: 12px;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.2), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
            width: 100%;
            max-width: 420px;
            padding: 36px 32px;
        }
        .login-header {
            text-align: center;
            margin-bottom: 28px;
        }
        .login-icon {
            width: 52px;
            height: 52px;
            background: linear-gradient(135deg, #2563eb, #3b82f6);
            border-radius: 12px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-size: 26px;
            color: #ffffff;
            margin-bottom: 12px;
        }
        .login-header h2 {
            font-size: 22px;
            font-weight: 700;
            color: #0f172a;
        }
        .login-header p {
            font-size: 13px;
            color: #64748b;
            margin-top: 4px;
        }
        .demo-credentials {
            background-color: #f1f5f9;
            border-left: 4px solid #2563eb;
            padding: 10px 14px;
            border-radius: 6px;
            font-size: 12px;
            color: #334155;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="login-card">
        <div class="login-header">
            <div class="login-icon">📚</div>
            <h2>LibraryHub Admin</h2>
            <p>Library Management System Portal</p>
        </div>

        <jsp:include page="/includes/messages.jsp" />

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-group" style="margin-bottom: 16px;">
                <label for="username" class="form-label">Username</label>
                <input type="text" id="username" name="username" class="form-control" 
                       placeholder="Enter username" required autofocus autocomplete="username">
            </div>

            <div class="form-group" style="margin-bottom: 24px;">
                <label for="password" class="form-label">Password</label>
                <input type="password" id="password" name="password" class="form-control" 
                       placeholder="Enter password" required autocomplete="current-password">
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; justify-content: center; padding: 10px; font-size: 14px;">
                Sign In to Dashboard &rarr;
            </button>
        </form>
    </div>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
