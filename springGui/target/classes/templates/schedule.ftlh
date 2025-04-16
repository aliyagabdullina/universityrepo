<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet" />
    <link rel="stylesheet" href="/styles/styleDataPages.css" />
  </head>
  <body>
    <section>
      <div class="container">
        <div class="menu">
          <h1>Scheduling</h1>
          <p class="university">UNIVERSITY</p>
          <div class="menu-buttons-container">
            <button class="menu-button icon" onclick="window.location.href='/account'">
              <img src="/images/account.svg" alt="Аккаунт" width="25" height="25" />
              <h4>Аккаунт</h4>
            </button>
            <button class="menu-button icon highlight" onclick="window.location.href='/schedule'">
              <img src="/images/scheduling.svg" alt="Расписание" width="25" height="25" />
              <h4>Расписание</h4>
            </button>
            <button class="menu-button icon" onclick="window.location.href='/data'">
              <img src="/images/data.svg" alt="Данные" width="25" height="25" />
              <h4>Данные</h4>
            </button>
            <button class="menu-button icon" onclick="window.location.href='/timeslot'">
              <img src="/images/time.svg" alt="Сетка пар" width="25" height="25" />
              <h4>Сетка пар</h4>
            </button>
          </div>
          <div class="menu-buttons-container-bottom">
            <button class="menu-button icon" onclick="window.location.href='/settings'">
              <img src="/images/setting.svg" alt="Настройки" width="25" height="25" />
              <h4>Настройки</h4>
            </button>
          </div>
        </div>

        <div class="frame">
          <div class="header">
            <h2>Расписание</h2>
            <div class="select-block select-block-schedule">
              <select id="categorySelect">
                <option value="" selected disabled>Категория</option>
                <option value="student">Студент</option>
                <option value="group">Группа</option>
                <option value="teacher">Преподаватель</option>
                <option value="place">Кабинет</option>
              </select>
            </div>
            <div class="spacer"></div>
            <div class="search-wrapper">
              <input type="text" id="searchInput" class="select-block select-block-schedule" placeholder="Поиск" disabled />
              <div id="results" class="search-results"></div>
            </div>
          </div>

          <div class="main">
            <div id="schedule-title" class="centre-text">
              <#if schedule??>
                <h3>${name}</h3>
              </#if>
            </div>
            <div id="schedule-table" class="table-style table-program">
              <#if schedule??>
                <table>
                  <thead>
                    <tr>
                      <th></th>
                      <#list DAYS as day>
                        <th>${day}</th>
                      </#list>
                    </tr>
                  </thead>
                  <tbody>
                    <#list LESSONS as lesson>
                      <tr>
                        <th>${lesson}</th>
                        <#list DAYS as day>
                          <td class="table-cell">
                            <#if schedule[lesson]?? && schedule[lesson][day]??>
                              <div class="cell-filled">${schedule[lesson][day]}</div>
                            </#if>
                          </td>
                        </#list>
                      </tr>
                    </#list>
                  </tbody>
                </table>
              <#else>
                <p>Расписание не найдено</p>
              </#if>
            </div>
          </div>
        </div>
      </div>
    </section>
  </body>
</html>
