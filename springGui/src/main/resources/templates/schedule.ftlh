<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="/styles/styleDataPages.css">
        <link rel="stylesheet" href="/styles/styleRectangle.css">
    </head>
    <body>
        <selection>
            <div class="container">
                <div class="menu">
                    <h1>Scheduling</h1>
                    <p class="university">UNIVERSITY</p>
                    <div class="menu-buttons-container">
                        <button class="menu-button icon" onclick="window.location.href='/account'">
                            <img src="/images/account.svg" alt="Аккаунт" width="25" height="25">
                            <h4>Аккаунт</h4>
                        </button>
                        <button class="menu-button icon highlight" onclick="window.location.href='/schedule'">
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
                        <h2>Расписание</h2>
                        <div class="sorting-buttons">
                          <div class="select-block select-block-schedule">
                          <form id="groupForm">
                            <select id="groupSelect" onchange="redirectToGroupPage()">
                              <option value="">Группа</option>
                              <option value="/schedule/group">Остапец Валерия</option>
                              <option value="/schedule/group">Габдуллина Алия</option>
                              <option value="/schedule/group">Соколова Александра</option>
                              <!-- Добавьте другие учителей по мере необходимости -->
                            </select>
                          </form>
                      </div>
                          <script>
                            function redirectToGroupPage() {
                              var selectElement = document.getElementById("groupSelect");
                              var selectedGroup = selectElement.value;
                              if (selectedGroup) {
                                window.location.href = selectedGroup;
                              }
                            }
                          </script>
                                                      <div class="spacer">

                          </div>
                        <div class="select-block select-block-schedule">
                          <form id="teacherForm">
                            <select id="teacherSelect" onchange="redirectToTeacherPage()">
                              <option value="">Учитель</option>
                              <option value="/schedule/teacher">Остапец Валерия</option>
                              <option value="/schedule/teacher">Габдуллина Алия</option>
                              <option value="/schedule/teacher">Соколова Александра</option>
                              <!-- Добавьте другие учителей по мере необходимости -->
                            </select>
                          </form>
                      </div>
                          <script>
                            function redirectToTeacherPage() {
                              var selectElement = document.getElementById("teacherSelect");
                              var selectedTeacher = selectElement.value;
                              if (selectedTeacher) {
                                window.location.href = selectedTeacher;
                              }
                            }
                          </script>
                          <div class="spacer">

                          </div>
                          <div class="select-block select-block-schedule">
                          <form id="placeForm">
                            <select id="placeSelect" onchange="redirectToPlacePage()">
                              <option value="">Кабинет</option>
                              <option value="/schedule/place">Остапец Валерия</option>
                              <option value="/schedule/place">Габдуллина Алия</option>
                              <option value="/schedule/place">Соколова Александра</option>
                              <!-- Добавьте другие учителей по мере необходимости -->
                            </select>
                          </form>
                      </div>
                          <script>
                            function redirectToPlacerPage() {
                              var selectElement = document.getElementById("placeSelect");
                              var selectedPlace = selectElement.value;
                              if (selectedPlace) {
                                window.location.href = selectedPlace;
                              }
                            }
                          </script>
                      </div>
                    </div>
                    <div class="main">
                        <div class="centre-text">
                            <h2>Выберите группу или учителя или кабинет для отображения расписания</h2>
                        </div>
                        <div class="spacer-bottom"></div>
                    </div>
                </div>
            </div>
        </selection>
    </body>
</html>
