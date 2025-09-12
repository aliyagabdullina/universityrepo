<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="/styles/styleLogin2.css">
    </head>
  <body>
    <div class="main">
      <h2 class="blue">Scheduling</h2><br />

      <form method="post" action="/login">
        <div class="space">
          <label for="username">Почта</label>
          <input type="text" id="username" name="username" placeholder="Введите почту" required />
        </div>

        <div class="space">
          <label for="password">Пароль</label>
          <input type="password" id="password" name="password" placeholder="Введите пароль" required />
        </div>

        <div class="space centre-text remember-block">
          <input type="checkbox" id="remember-me" name="remember-me" class="remember-checkbox" />
          <label for="remember-me" class="remember-label">Запомнить меня</label>
        </div>

        <div class="space"></div>
        <div class="space"></div><br />

        <button type="submit" class="login-button space">Войти</button>

        <div class="error" id="errorMsg" style="display: none;">Ошибка! Неверный логин или пароль</div>
      </form>
    </div>
  </body>
</html>