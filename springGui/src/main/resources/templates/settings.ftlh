<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="/styles/styleDataPages.css">
        <link rel="stylesheet" href="/styles/styleSettings.css">
    </head>
    <body>
        <section>
            <div class="container">
                <div class="menu">
                    <h1>Scheduling</h1>
                    <div class="menu-buttons-container">
                        <button class="menu-button icon" onclick="window.location.href='/account'">
                            <img src="/images/account.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Аккаунт</h4>
                        </button>
                        <button class="menu-button icon" onclick="window.location.href='/timeslot'">
                            <img src="/images/time.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Тайм слоты</h4>
                        </button>
                        <button class="menu-button icon" onclick="window.location.href='/places'">
                            <img src="/images/data.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Редактирование данных</h4>
                        </button>
                        <button class="menu-button icon" onclick="window.location.href='/schedule'">
                            <img src="/images/scheduling.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Расписание</h4>
                        </button>
                    </div>
                    <div class="menu-buttons-container-bottom">
                        <button class="menu-button icon highlight" onclick="window.location.href='/settings'">
                            <img src="/images/setting.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Настройки</h4>
                        </button>
                    </div>
                </div>
                <div></div>
                <div class="frame">
                    <div class="header">
                        <h2>Настройки</h2>
                    </div>
                    <div>
                        <div class="line">
                            <button class="button-settings"><h4>Инструкции</h4></button>
                            <div class="spacer-width"></div>
                            <button class="button-settings" onclick="redirectToSupport()"><h4>Служба поддержки</h4></button>
                        </div>
                        <div class="spacer-height"></div>
                        <div class="line">
                            <button class="button-settings"><h4>Условия пользования</h4></button>
                            <div class="spacer-width"></div>
                            <button class="button-settings"><h4>Политика пользования</h4></button>
                        </div>
                    </div>
                    <div>
                        <h2>Тарифы</h2>
                    </div>
                    <div class="tariff-info">
                        <h3>Тарифный план "Профессионал"</h3>
                        <p><h4>Дата покупки: 12 апреля 2024</h4></p>
                        <p><h4>Дата окончания: 12 апреля 2025</h4></p>
                        <p><h4>Описание: Подходит для крупных образовательных учреждений и университетов, где требуется высокая производительность и масштабируемость.</h4></p>
                        <button class="button-settings button-tariff"><h4>Сменить</h4></button>
                    </div>
                </div>
            </div>
        </section>
        <script>
            function redirectToSupport() {
                alert("Сейчас вы будете перенаправлены на звонок в службу поддержки.");
                setTimeout(function() {
                    window.location.href = "tel:+79220501003";
                }, 1000); // 1000 миллисекунд = 1 секунда задержки
            }
        </script>
    </body>
</html>
