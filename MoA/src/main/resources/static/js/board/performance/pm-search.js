document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('boardSearch');
    const applyBtn = document.querySelector('.apply-btn');
    const searchInput = document.querySelector('#query');
    const params = new URLSearchParams(window.location.search);

    // 1️⃣ 검색어 유지
    const keyword = params.get('query');
    if (keyword) searchInput.value = keyword;

    // 2️⃣ 체크박스 상태 유지
    params.forEach((value, key) => {
        document.querySelectorAll(`.filter-group[data-filter="${key}"] input[type="checkbox"]`)
            .forEach(cb => {
                if (cb.dataset.value === value) cb.checked = true;
            });
    });

    // 3️⃣ 그룹 내부에서만 “전체” 제어
    document.querySelectorAll('.filter-group').forEach(group => {
        const allBox = group.querySelector('input[data-value="all"]');
        const checkboxes = group.querySelectorAll('input[type="checkbox"]:not([data-value="all"])');

        if (!allBox) return; // 전체가 없는 그룹은 스킵

        // “전체” 클릭 시, 같은 그룹의 나머지만 해제
        allBox.addEventListener('change', (e) => {
            if (e.target.checked) {
                checkboxes.forEach(cb => (cb.checked = false));
            }
        });

        // 나머지 체크 시, 같은 그룹의 “전체”만 해제
        checkboxes.forEach(cb => {
            cb.addEventListener('change', (e) => {
                if (e.target.checked) {
                    allBox.checked = false;
                }
            });
        });
    });

    // 4️⃣ “반영하기” 버튼 클릭 시
    applyBtn.addEventListener('click', (e) => {
        e.preventDefault();
        const newParams = new URLSearchParams();

        document.querySelectorAll('.filter-group').forEach(group => {
            const key = group.dataset.filter;
            const selected = [...group.querySelectorAll('input[type="checkbox"]:checked')]
                .map(el => el.dataset.value)
                .filter(v => v !== 'all');
            selected.forEach(v => newParams.append(key, v));
        });

        const keyword = searchInput.value.trim();
        if (keyword) newParams.set('query', keyword);

        window.location.href = form.getAttribute('action') + '?' + newParams.toString();
    });
});
