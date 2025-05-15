<form method="post" action="/login">
    <div class="space">
        <label for="username">Почта</label>
        <input type="text" id="username" name="username" placeholder="Введите почту" required>
    </div>

    <div class="space">
        <label for="password">Пароль</label>
        <input type="password" id="password" name="password" placeholder="Введите пароль" required>
    </div>

    <div class="space centre-text remember-block">
        <input type="checkbox" id="remember-me" name="remember-me" class="remember-checkbox">
        <label for="remember-me" class="remember-label">Запомнить меня</label>
    </div>

    <button type="submit" class="login-button space">Войти</button>

    <div class="error" id="errorMsg" style="display: none;">Ошибка! Неверный логин или пароль</div>
</form>
