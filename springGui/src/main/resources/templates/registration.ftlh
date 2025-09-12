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
        <form action="/login" method="post" onsubmit="return validateForm(event)">
            <div class="space">
                <label for="name">ФИО</label>
                <input class="input-max" type="text" id="name" name="name" placeholder="Введите ФИО" required>
            </div>

            <div class="space">
                <label for="university">Название университета</label>
                <div class="custom-select">
                    <select id="university" name="university" required>
                        <option value="" disabled selected>Выберите университет</option>
                        <option value="1">МГУ</option>
                        <option value="2">СПбГУ</option>
                        <option value="3">НГУ</option>
                        <option value="4">КФУ</option>
                    </select>
                </div>
            </div>

            <div class="space">
                <label for="role">Должность</label>
                <div class="custom-select">
                    <select id="role" name="role" required>
                        <option value="" disabled selected>Выберите должность</option>
                        <option value="1">Студент</option>
                        <option value="2">Преподаватель</option>
                        <option value="3">Администратор</option>
                        <option value="4">Декан</option>
                    </select>
                </div>
            </div>

            <div class="space">
                <label for="faculty">Факультет</label>
                <div class="custom-select">
                    <select id="faculty" name="faculty" required>
                        <option value="" disabled selected>Выберите факультет</option>
                        <option value="1">Физический</option>
                        <option value="2">Математический</option>
                        <option value="3">Экономический</option>
                        <option value="4">Филологический</option>
                    </select>
                </div>
            </div>

            <div class="space">
                <label for="email">Почта</label>
                <input class="input-max" type="text" id="email" name="email" placeholder="Введите почту" required>
            </div>

            <div class="space">
                <label for="password">Пароль</label>
                <input class="input-max" type="password" id="password" name="password" placeholder="Введите пароль" required>
            </div>

            <div class="space"></div><br><div class="space"></div>
            <button type="submit" class="login-button">
                <h4>Registration</h4>
            </button>
            <div class="error" id="errorMsg">Ошибка! Неверные данные</div>
        </form>
    </div>

    <script>
    // Валидация формы при отправке
    function validateForm(event) {
        event.preventDefault();

        const errorMsg = document.getElementById("errorMsg");
        const name = document.getElementById("name").value;
        const university = document.getElementById("university").value;
        const role = document.getElementById("role").value;
        const faculty = document.getElementById("faculty").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        if (name && university && role && faculty && email && password) {
            errorMsg.style.display = "none";
            window.location.href = '/courses'; // Успешный переход
        } else {
            errorMsg.style.display = "block"; // Показываем ошибку
        }

        return false;
    }

    // Обновление цвета текста у <select> при выборе значения
    document.addEventListener("DOMContentLoaded", () => {
        document.querySelectorAll('.custom-select select').forEach(select => {
            // Начальная проверка при загрузке
            if (select.value) {
                select.classList.add('has-value');
            }

            // Добавляем/удаляем класс при изменении значения
            select.addEventListener('change', () => {
                if (select.value) {
                    select.classList.add('has-value');
                } else {
                    select.classList.remove('has-value');
                }
            });
        });
    });
</script>

</body>
</html>