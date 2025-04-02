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
                    <button class="menu-button icon highlight" onclick="window.location.href='/account'">
                        <img src="/images/account.svg" alt="Аккаунт" width="25" height="25">
                        <h4>Аккаунт</h4>
                    </button>
                    <button class="menu-button icon" onclick="window.location.href='/schedul'">
                        <img src="/images/scheduling.svg" alt="Аккаунт" width="25" height="25">
                        <h4>Расписание</h4>
                    </button>
                    <button class="menu-button icon" onclick="window.location.href='/places'">
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
            <div class="frame">
                <div class="header">
                    <h2>Данные</h2>
                    <div class="spacer"></div>
                    <div class="custom-select">
                        <select onchange="if(this.value === '1') window.location.href='/places';
                                        else if(this.value === '2') window.location.href='/subjects';
                                        else if(this.value === '3') window.location.href='/teachers';
                                        else if(this.value === '4') window.location.href='/students';
                                        else if(this.value === '5') window.location.href='/groups';
                                        else if(this.value === '6') window.location.href='/programs';
                                        else if(this.value === '7') window.location.href='/plan'">
                            <option value="" disabled selected class="placeholder">Раздел</option>
                            <option value="1">Аудитории</option>
                            <option value="2">Предметы</option>
                            <option value="3">Преподаватели</option>
                            <option value="4">Студенты</option>
                            <option value="5">Группы</option>
                            <option value="6">Программы</option>
                            <option value="7">Индивидуальный план</option>
                        </select>
                    </div>
                </div>
                <div class="main"></div>
                <div class="footer"></div>
            </div>
        </div>
    </section>
</body>
</html>