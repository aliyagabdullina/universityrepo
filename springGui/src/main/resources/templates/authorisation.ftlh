<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="/styles/styleLogin2.css">
</head>
<body>
    <div class="main">
        <h2 class="blue">Scheduling</h2><br>
        <form id="loginForm" onsubmit="return validateForm(event)">
            <div class="space">
                <label for="username">Почта</label>
                <input type="text" id="username" name="username" placeholder="Введите почту" required>
            </div>

            <div class="space">
                <label for="password">Пароль</label>
                <input type="password" id="password" name="password" placeholder="Введите пароль" required>
            </div>

            <div class="space centre-text remember-block">
              <input type="checkbox" id="remember" name="remember" class="remember-checkbox">
              <label for="remember" class="remember-label">Запомнить меня</label>
          </div>
          <div class="space"></div><div class="space"></div><br>
          <button type="submit" class="login-button space">Войти</button>
            <div class="error" id="errorMsg">Ошибка! Неверный логин или пароль</div>
        </form>
    </div>

    <script>
        function validateForm(event) {
            event.preventDefault(); // Предотвращаем отправку формы

            // Пример правильных учетных данных (для демонстрации)
            const correctUsername = "test_login";
            const correctPassword = "test_password";

            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;
            const errorMsg = document.getElementById("errorMsg");

            // Проверка логина и пароля
            if (username === correctUsername && password === correctPassword) {
                errorMsg.style.display = "none";
                alert("Успешная авторизация!");
                window.location.href = '/account';
            } else {
                errorMsg.style.display = "block";
            }

            return false; // Предотвращаем стандартную отправку
        }
    </script>
</body>
</html>