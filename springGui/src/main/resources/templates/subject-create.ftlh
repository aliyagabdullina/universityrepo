<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="/styles/styleDataPages.css">
    </head>

<body>
    <section>
        <div class="container">
            <div class="menu">
                <h1>Scheduling</h1>
                <p class="university">UNIVERSITY</p>
                <div class="menu-buttons-container">
                    <button class="menu-button icon" onclick="window.location.href='/account'">
                        <img src="/images/account.svg" alt="Аккаунт" width="25" height="25">
                        <h4>Аккаунт</h4>
                    </button>
                    <button class="menu-button icon" onclick="window.location.href='/schedule'">
                        <img src="/images/scheduling.svg" alt="Аккаунт" width="25" height="25">
                        <h4>Расписание</h4>
                    </button>
                    <button class="menu-button icon highlight" onclick="window.location.href='/places'">
                        <img src="/images/data.svg" alt="Аккаунт" width="25" height="25">
                        <h4>Данные</h4>
                    </button>
                    <button class="menu-button icon" onclick="window.location.href='/timeslot'">
                        <img src="/images/time.svg" alt="Аккаунт" width="25" height="25">
                        <h4>Сетка пар</h4>
                    </button>
                </div>
                <div class="menu-buttons-container-bottom">
                    <button class="menu-button icon" onclick="window.location.href='/settings'">
                        <img src="/images/setting.svg" alt="Аккаунт" width="25" height="25">
                        <h4>Настройки</h4>
                    </button>
                </div>
            </div>
            <div></div>
            <div class="body">
                <div class="header-create icon-back">
                    <h2>Редактирование данных</h2>
                    <button class="button-none" type="button" onclick="window.history.back()">
                        <img src="/images/back.svg" alt="Назад" width="25" height="25">
                    </button>
                </div>
                <div class="main">
                    <form action="/place/create" method="post" onsubmit="return handleFormSubmit()">
                        <div class="input-block">
                            <h5>Предмет</h5>
                            <input type="text" name="name" placeholder="Введите предмет" required>
                        </div>
                        <div class="input-block">
                            <h5>Кол-во пар в неделю</h5>
                            <input type="text" name="pairsPerWeek" placeholder="Введите кол-во пар в неделю" required>
                        </div>
                        <div class="input-block">
                            <h5>Кол-во пар в день</h5>
                            <input type="text" name="pairsPerDay" placeholder="Введите кол-во пар в день" required>
                        </div>

                        <div class="footer">
                            <div class="footer-buttons-container">

                                <button class="footer-button" type="button" onclick="window.history.back()">
                                    <h4>Назад</h4>
                                </button>
                              <div class="footer-right">
                                    <button type="submit" class="footer-button save-button">
                                        <h4>Сохранить</h4>
                                    </button>
                              </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </section>

    <script>
        function toggleCustomInput() {
            const select = document.getElementById("typeSelect");
            const customInput = document.getElementById("customTypeInput");
            if (select.value === "Другое") {
                customInput.style.display = "inline-block";
                customInput.required = true;
            } else {
                customInput.style.display = "none";
                customInput.required = false;
                customInput.value = ""; // Очищаем поле, если выбрано не "Другое"
            }
        }

        function handleFormSubmit() {
            const select = document.getElementById("typeSelect");
            const customInput = document.getElementById("customTypeInput");
            const descriptionField = document.getElementsByName("description")[0];

            // Если выбрано "Другое", используем значение из input
            if (select.value === "Другое") {
                descriptionField.value = customInput.value;
            }
            return true; // Отправляем форму
        }
    </script>
</body>
</html>