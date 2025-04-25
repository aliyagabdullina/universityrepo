document.addEventListener('DOMContentLoaded', function () {
        fetch('/api/schedule')  // Выполняем запрос к серверу для получения данных
          .then(response => response.json())
          .then(data => {
            console.log("Полученные данные: ", data); // Отладка
            // Извлекаем данные из ответа и передаем их в нужные переменные
            const teacherList = data.teachers;
            const studentList = data.students;
            const groupList = data.groups;
            const placeList = data.places;
            const groupScheduleMap = data.groupScheduleMap;

            const categorySelect = document.getElementById('categorySelect');
            const searchInput = document.getElementById('searchInput');
            const resultsContainer = document.getElementById('results');

            const categoryMap = {
              teacher: teacherList,
              student: studentList,
              group: groupList,
              place: placeList
            };

            categorySelect.addEventListener('change', () => {
              searchInput.disabled = false;
              searchInput.value = "";
              searchInput.placeholder = "Введите запрос...";
              resultsContainer.innerHTML = "";
              resultsContainer.style.display = "none";
            });

            searchInput.addEventListener('input', () => {
              const query = searchInput.value.toLowerCase();
              const selectedCategory = categorySelect.value;
              const list = categoryMap[selectedCategory] || [];

              const results = list.filter(item =>
                item.name && item.name.toLowerCase().includes(query)
              );

              showResults(results, selectedCategory);
            });

            function showResults(results, entityType) {
              resultsContainer.innerHTML = "";

              if (results.length === 0) {
                resultsContainer.style.display = "none";
                return;
              }

              resultsContainer.style.display = "block";

              results.forEach(item => {
                const el = document.createElement("div");
                el.className = "result-item";
                el.textContent = item.name;
                el.onclick = () => {
                  resultsContainer.style.display = "none";
                  searchInput.value = item.name;
                  if (entityType === 'group') {
                    renderSchedule("group", item.name, groupScheduleMap);
                  }
                };
                resultsContainer.appendChild(el);
              });
            }

            const DAYS = ["ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ"];
            const LESSONS = [1, 2, 3, 4, 5, 6, 7, 8];

            function renderSchedule(entityType, entityName, scheduleMap) {
              const schedule = scheduleMap[entityName];
              console.log("Schedule:", schedule);
              if (!schedule) {
                document.getElementById("schedule-table").innerHTML = "<p>Расписание не найдено</p>";
                return;
              }

              const tableData = {};
              LESSONS.forEach(lesson => {
                tableData[lesson] = {};
                DAYS.forEach(day => {
                  tableData[lesson][day] = "";
                });
              });

              schedule.forEach(entry => {
                const day = entry.dayOfWeek;
                const lesson = entry.lessonNumber;

                let content = `${entry.course}<br>${entry.place}<br>${entry.teacher}`;
                tableData[lesson][day] = content;
              });
              console.log("table:", tableData);

              let html = "<table border='1'><thead><tr><th>Урок</th>";

    // Заголовки дней недели
    DAYS.forEach(day => {
        html += `<th>${day}</th>`;
    });
    html += "</tr></thead><tbody>";

    // Формируем строки с уроками и днями недели
    LESSONS.forEach(lesson => {
        html += `<tr><th>${lesson}</th>`;  // Добавляем номер урока

        // Добавляем содержимое ячеек для каждого дня недели
        DAYS.forEach(day => {
            const entry = schedule.find(e => e.dayOfWeek === day && e.lessonNumber === lesson);
            const content = entry ? `${entry.course}<br>${entry.place}<br>${entry.teacher}` : "";
            html += `<td>${content || "&nbsp;"}</td>`;
        });
        html += "</tr>";
    });

    html += "</tbody></table>";
              console.log("html:", html);
              document.getElementById("schedule-title").innerHTML = `<h3>${entityName}</h3>`;
              document.getElementById("schedule-table").innerHTML = html;
            }
          });
      });