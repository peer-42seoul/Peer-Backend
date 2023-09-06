# Peer-Backend
Peer service backend repository


<h2>Commit Convention</h2>
<h3>양식</h3>
<pre><code>&lt;type&gt;: &lt;code&gt; &lt;subject&gt; &lt;#issueNumber&gt;
</code></pre>
<ul>
<li><strong>type</strong> : 커밋 의도</li>
<li>code : 기능코드</li>
<li><strong>subject</strong> : 영어로 작성한 커밋의 제목
<ul>
<li>제목은 영어 기준 50자 이내, 한글 사용 금지</li>
<li>목적어와 행위를 분명하게 명시하기 (과거 시제를 사용하지 않기)</li>
<li>제목 끝에 <code>.</code> 는 금지</li>
<li>제목은 명령어, 개조식으로 작성</li>
<li><strong>issueNumber</strong> : 해당 이슈 티켓넘버입니다. '#' 기호와 이슈 번호를 붙여 표현합니다.</li>
</ul>
</li>
</ul>
<h3>Type</h3>


Prefix Type | 설명
-- | --
Feat | 새로운 기능을 추가할 경우
Fix | 버그를 고친 경우
Style | 코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우
Refactor | 프로덕션 코드 리팩토링
Comment | 필요한 주석 추가 및 변경
Docs | 문서를 수정한 경우
Test | 테스트 추가, 테스트 리팩토링 (코드 변경 X)
Chore | 빌드 테스트 업데이트, 패키지 매니저를 설정하는 경우 (코드 변경 X)
Rename | 파일 혹은 폴더명을 수정하거나 옮기는 작업만인 경우
Remove | 파일을 삭제하는 작업만 수행한 경우


<h3>예시</h3>
<pre><code>feat: A-JOI-01 게시글 작성 API 구현

fix: A-JOI-01 게시글 수정 API에서 게시글이 수정되지 않는 버그 수정
</code></pre>
<h2>Pull Request 컨벤션</h2>
<h3>convention 원칙</h3>
<ul>
<li>PR의 의도에 맞는 커밋만 추가하며 PR의 의도와 다른 작업은 추가적으로 issue와 PR을 생성합니다.</li>
<li>본문은 반드시 템플릿을 활용하여 작성합니다.</li>
<li>PR 요청 후 추가 작업이 생겼을 경우는 요청을 close하고 작업이 완전히 종료된 후 다시 요청합니다.</li>
</ul>
<h3>제목양식</h3>
<pre><code>
[&lt;type&gt;] &lt;subject&gt; &lt;#issueNumber&gt;
</code></pre>
<h3>본문양식</h3>
<pre><code>

### 변경 사항
ex) 로그인 시, 구글 소셜 로그인 기능을 추가했습니다.

### 테스트 결과
ex) 베이스 브랜치에 포함되기 위한 코드는 모두 정상적으로 동작해야 합니다. 결과물에 대한 스크린샷, GIF, 혹은 라이브 데모가 가능하도록 샘플API를 첨부할 수도 있습니다.

</code></pre>
<ul>
<li><strong>type</strong> : 어떤 의도의 커밋인지를 나타냅니다. 첫글자를 대문자로 작성합니다.</li>
<li><strong>subject</strong> : 커밋의 제목입니다. 20자를 넘기지 않도록 합니다.</li>
<ul>
<li>제목은 한글 기준 20자 이내</li>
<li>목적어와 행위를 분명하게 명시하기 (과거 시제를 사용하지 않기)</li>
<li>제목 끝에 <code>.</code> 는 금지</li>
<li>제목은 명령어, 개조식으로 작성</li>
</ul>
<li><strong>issueNumber</strong> : 해당 이슈 티켓넘버입니다. '#' 기호와 이슈 번호를 붙여 표현합니다.</li>
</li>
</ul>
<h3>Type</h3>
<ul>
<li>PR의 Type 종류는 commit 컨벤션의 type과 동일합니다.</li>
<li>반영 내용을 대표할 수 있는 type으로 선택합니다.</li>
</ul>
