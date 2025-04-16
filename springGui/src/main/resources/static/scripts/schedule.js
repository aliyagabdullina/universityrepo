
      document.addEventListener('DOMContentLoaded', function () {
      console.log("Загруженные данные")
        const teacherList = [ { name: "Иванов" }, { name: "Петров" } ];
        const studentList = [ { name: "Сидоров А." }, { name: "Кузнецова И." } ];
        const groupList = [ { name: "БПИ214" }, { name: "БПИ222" } ];
        const placeList = [ { name: "101" }, { name: "202" } ];

        const groupScheduleMap = {
          "БПИ214": [
            {
              timeslot: { dayOfWeek: "ПН", lessonNumber: 1 },
              course: "Математика",
              teacher: "Иванов",
              place: "101"
            }
          ]
        };

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
                    item.name.toLowerCase().includes(query)
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
                    const day = entry.timeslot.dayOfWeek;
                    const lesson = entry.timeslot.lessonNumber;

                    let content = ${entry.course}<br>${entry.place}<br>${entry.teacher};
                    tableData[lesson][day] = content;
                  });

                  let html = "<table><thead><tr><th></th>";
                  DAYS.forEach(day => {
                    html += <th>${day}</th>;
                  });
                  html += "</tr></thead><tbody>";

                  LESSONS.forEach(lesson => {
                    html += <tr><th>${lesson}</th>;
                    DAYS.forEach(day => {
                      const cell = tableData[lesson][day];
                      html += <td class="table-cell">${cell ? `<div class="cell-filled">${cell}</div> : ""}</td>`;
                    });
                    html += "</tr>";
                  });

                  html += "</tbody></table>";

                  document.getElementById("schedule-title").innerHTML = <h3>${entityName}</h3>;
                  document.getElementById("schedule-table").innerHTML = html;
                }
              });
