document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('boardSearch');
    const btn = document.querySelector('.search-btn');
    const searchInput = document.querySelector('#query');
    const params = new URLSearchParams(window.location.search);

    // 1 검색어 유지
    const keyword = params.get('query');
    if (keyword) searchInput.value = keyword;

    // 2 체크박스 상태 유지
    params.forEach((value, key) => {
        document.querySelectorAll(`.filter-group[data-filter="${key}"] input[type="checkbox"]`)
            .forEach(check => {
                if (check.dataset.value === value) check.checked = true;
            });
    });

    // 3 같은 카테고리 내에서 전체 체크박스 클릭 시 
    document.querySelectorAll('.filter-group').forEach(group => {
        const allBox = group.querySelector('input[data-value="all"]');
        const checkboxes = group.querySelectorAll('input[type="checkbox"]:not([data-value="all"])');

        if (!allBox) return; // 전체가 없는 그룹은 스킵

        // “전체” 클릭 시, 같은 그룹의 나머지만 해제
        allBox.addEventListener('change', (e) => {
            if (e.target.checked) {
                checkboxes.forEach(check => (check.checked = false));
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

    // 가격 체크박스 단일선택으로 제한
    const priceGroup = document.querySelector('.filter-group[data-filter="price"]');
    if (priceGroup) {
        const priceCheckboxes = priceGroup.querySelectorAll('input[type="checkbox"]:not([data-value="all"])');

        priceCheckboxes.forEach(cb => {
            cb.addEventListener('change', (e) => {
                if (e.target.checked) {
                    // 자신 외 모든 체크 해제
                    priceCheckboxes.forEach(other => {
                        if (other !== cb) other.checked = false;
                    });
                }
            });
        });
    }

    // 기간 체크박스 단일선택으로 제한
    const dateGroup = document.querySelector('.filter-group[data-filter="date"]');
    if (dateGroup) {
        const dateCheckboxes = dateGroup.querySelectorAll('input[type="checkbox"]:not([data-value="all"])');
        
        dateCheckboxes.forEach(cb => {
            cb.addEventListener('change', (e) => {
                if (e.target.checked) {
                    // 자신 외 모든 체크 해제
                    dateCheckboxes.forEach(other => {
                        if (other !== cb) other.checked = false;
                    });
                }
            });
        });
    }

    // 4 “반영하기” 버튼 클릭 시
    btn.addEventListener('click', (e) => {
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

        window.location.href = '/board/4/pmSearchList?' + newParams.toString();

    });

    // 5 전체 체크박스 조건
    document.querySelectorAll('.filter-group').forEach(group => {
        const allBox = group.querySelector('input[data-value="all"]');
        const others = group.querySelectorAll('input[type="checkbox"]:not([data-value="all"])');

        if (!allBox) return;

        // 그룹 상태 갱신 함수
        const updateAllState = () => {
            const anyChecked = Array.from(others).some(cb => cb.checked);
            allBox.checked = !anyChecked; // 전부 해제면 전체 체크
        };

        // 초기 로드 시 상태 보정
        updateAllState();

        // 개별 체크박스 상태 변경 시 즉시 반응
        others.forEach(cb => {
            cb.addEventListener('change', () => {
                updateAllState();
            });
        });

        // 전체박스 클릭 시 나머지 해제
        allBox.addEventListener('change', (e) => {
            if (e.target.checked) {
                others.forEach(cb => cb.checked = false);
            }
            updateAllState();
        });
    });
});
