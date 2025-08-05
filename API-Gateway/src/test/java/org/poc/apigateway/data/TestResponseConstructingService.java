package org.poc.apigateway.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.poc.apigateway.entities.Member;
import org.poc.apigateway.gitlab_api_client.GitLabApiClientService;
import org.poc.apigateway.gitlab_api_client.matcher.FileCommitAuthorMatcher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestResponseConstructingService {
    @SuppressWarnings("all")
    private final String membersExample = """
            [
                {
                    "id": 2410870,
                    "username": "g0tmi1k",
                    "publicEmail": "",
                    "accessLevel": 50,
                    "webUrl": "https://gitlab.com/g0tmi1k"
                }
            ]
            """;
    @SuppressWarnings("all")
    private final String mrsExample = """
            [
                {
                    "id": 223526620,
                    "iid": 31,
                    "authorId": 2410870,
                    "title": "Add  kali-system-cli to kali-linux-headless",
                    "state": "merged",
                    "targetBranch": "kali/master",
                    "createdAt": "2023-05-13T09:08:04.425Z",
                    "mergedAt": "2023-05-15T06:05:05.859Z",
                    "webUrl": "https://gitlab.com/kalilinux/packages/kali-meta/-/merge_requests/31"
                },
                {
                    "id": 223526254,
                    "iid": 30,
                    "authorId": 2410870,
                    "title": "Add kali-linux-wsl",
                    "state": "merged",
                    "targetBranch": "kali/master",
                    "createdAt": "2023-05-13T08:55:33.401Z",
                    "mergedAt": "2023-05-15T06:01:30.772Z",
                    "webUrl": "https://gitlab.com/kalilinux/packages/kali-meta/-/merge_requests/30"
                },
                {
                    "id": 207336016,
                    "iid": 29,
                    "authorId": 4897103,
                    "title": "Migrate kali-desktop-i3-gaps' dependencies to kali-desktop-i3",
                    "state": "closed",
                    "targetBranch": "kali/master",
                    "createdAt": "2023-02-23T09:52:46.583Z",
                    "mergedAt": null,
                    "webUrl": "https://gitlab.com/kalilinux/packages/kali-meta/-/merge_requests/29"
                }
            ]
            """;
    @Mock
    private GitLabApiClientService gitLabApiClientService;
    @Mock
    private FileCommitAuthorMatcher fileCommitAuthorMatcher;

    @InjectMocks
    private ResponseConstructingService responseConstructingService;

    @BeforeEach
    void setUp(){

    }

    @Test
    void testGetMembers() {
        when(gitLabApiClientService.getMembers()).thenReturn(membersExample);
        when(gitLabApiClientService.getMRsAll()).thenReturn(mrsExample);

        when(fileCommitAuthorMatcher.matchId(2410870)).thenReturn("g0tmi1k@kali.org");

        List<Member> members = responseConstructingService.getMembers();
        Member firstMember = members.getFirst();

        assertEquals(2410870, firstMember.id());
        assertEquals(1, members.size());
        assertEquals("g0tmi1k", firstMember.username());
        assertEquals("g0tmi1k@kali.org", firstMember.email());
        assertEquals(2, firstMember.createdMRs());
    }

    @Test
    void testGetMemberByEmail() {
        when(gitLabApiClientService.getMembers()).thenReturn(membersExample);
        when(gitLabApiClientService.getMRsAll()).thenReturn(mrsExample);

        when(fileCommitAuthorMatcher.matchEmail("gotmilk@kali.org")).thenReturn(2410870L);
        when(fileCommitAuthorMatcher.matchId(2410870)).thenReturn("g0tmi1k@kali.org");

        Member member = responseConstructingService.getMemberByEmail("gotmilk@kali.org");

        assertEquals(2410870, member.id());
        assertEquals("g0tmi1k", member.username());
        assertEquals("g0tmi1k@kali.org", member.email());
        assertEquals(2, member.createdMRs());
    }
}
