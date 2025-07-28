package org.poc.gitlabapiclient.gitlabapi.data;

import org.junit.jupiter.api.Test;
import org.poc.gitlabapiclient.gitlabapi.data.enteties.Branch;
import org.poc.gitlabapiclient.gitlabapi.data.enteties.Commit;
import org.poc.gitlabapiclient.gitlabapi.data.enteties.Member;
import org.poc.gitlabapiclient.gitlabapi.data.enteties.MergeRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjectsCompressingServiceTest {

    private final ObjectsCompressingService service = new ObjectsCompressingService();
    @SuppressWarnings("all")
    private final String testMember = """
                [
                  {
                    "id": 1,
                    "username": "john",
                    "public_email": "john@example.com",
                    "access_level": 30,
                    "web_url": "https://gitlab.com/john"
                  },
                  {
                    "id": 2,
                    "username": "alice",
                    "public_email": "alice@example.com",
                    "access_level": 40,
                    "web_url": "https://gitlab.com/alice"
                  }
                ]
                """;
    @SuppressWarnings("all")
    private final String testMR = """
                [
                    {
                        "id": 385191603,
                        "iid": 36,
                        "project_id": 11903824,
                        "title": "Fix missing packages for watches",
                        "description": "",
                        "state": "merged",
                        "created_at": "2025-05-22T00:49:24.384Z",
                        "updated_at": "2025-05-23T00:03:52.223Z",
                        "merged_by": {
                            "id": 108450,
                            "username": "arnaudr",
                            "public_email": "",
                            "name": "Arnaud Rebillout",
                            "state": "active",
                            "locked": false,
                            "avatar_url": "https://gitlab.com/uploads/-/system/user/avatar/108450/avatar.png",
                            "web_url": "https://gitlab.com/arnaudr"
                        },
                        "merge_user": {
                            "id": 108450,
                            "username": "arnaudr",
                            "public_email": "",
                            "name": "Arnaud Rebillout",
                            "state": "active",
                            "locked": false,
                            "avatar_url": "https://gitlab.com/uploads/-/system/user/avatar/108450/avatar.png",
                            "web_url": "https://gitlab.com/arnaudr"
                        },
                        "merged_at": "2025-05-22T07:24:00.423Z",
                        "closed_by": null,
                        "closed_at": null,
                        "target_branch": "kali/master",
                        "source_branch": "patch-1",
                        "user_notes_count": 0,
                        "upvotes": 0,
                        "downvotes": 0,
                        "author": {
                            "id": 3924854,
                            "username": "yesimxev",
                            "public_email": "",
                            "name": "yesimxev",
                            "state": "active",
                            "locked": false,
                            "avatar_url": "https://gitlab.com/uploads/-/system/user/avatar/3924854/avatar.png",
                            "web_url": "https://gitlab.com/yesimxev"
                        },
                        "assignees": [],
                        "assignee": null,
                        "reviewers": [],
                        "source_project_id": 70061277,
                        "target_project_id": 11903824,
                        "labels": [
                            "Role::Packager",
                            "Topic::Packaging"
                        ],
                        "draft": false,
                        "imported": false,
                        "imported_from": "none",
                        "work_in_progress": false,
                        "milestone": {
                            "id": 4442036,
                            "iid": 28,
                            "group_id": 5034914,
                            "title": "Kali 2025.2",
                            "description": "https://www.kali.org/blog/kali-linux-2025-2-release/",
                            "state": "closed",
                            "created_at": "2024-02-07T19:31:03.794Z",
                            "updated_at": "2025-07-14T12:48:37.892Z",
                            "due_date": "2025-06-03",
                            "start_date": "2025-03-20",
                            "expired": true,
                            "web_url": "https://gitlab.com/groups/kalilinux/-/milestones/28"
                        },
                        "merge_when_pipeline_succeeds": false,
                        "merge_status": "can_be_merged",
                        "detailed_merge_status": "not_open",
                        "merge_after": null,
                        "sha": "8e3bd42c80e0b761fdd0312286f02bbdc2064a2a",
                        "merge_commit_sha": "af82aa5de39cf6a91e6fd7fcad2ee050d080265d",
                        "squash_commit_sha": "b1e456b494df0e7b6aae2eaca499fc724c49bad7",
                        "discussion_locked": null,
                        "should_remove_source_branch": null,
                        "force_remove_source_branch": true,
                        "prepared_at": "2025-05-22T00:49:32.080Z",
                        "allow_collaboration": true,
                        "allow_maintainer_to_push": true,
                        "reference": "!36",
                        "references": {
                            "short": "!36",
                            "relative": "!36",
                            "full": "kalilinux/packages/kali-meta!36"
                        },
                        "web_url": "https://gitlab.com/kalilinux/packages/kali-meta/-/merge_requests/36",
                        "time_stats": {
                            "time_estimate": 0,
                            "total_time_spent": 0,
                            "human_time_estimate": null,
                            "human_total_time_spent": null
                        },
                        "squash": true,
                        "squash_on_merge": true,
                        "task_completion_status": {
                            "count": 0,
                            "completed_count": 0
                        },
                        "has_conflicts": false,
                        "blocking_discussions_resolved": true,
                        "approvals_before_merge": null
                    },
                    {
                        "id": 350161477,
                        "iid": 35,
                        "project_id": 11903824,
                        "title": "Start separating security and system tools & add kali-purple meta packages",
                        "description": "* Prepare system packages:\\n  * - kali-system-core = minimum set of core system tools installed on all systems\\n  * - kali-system-cli  = default set of system tools installed on all but the most lean systems without gui\\n  * - kali-system-gui  = default set of system tools installed on all systems with gui\\n\\n  * Prepare Kali Purple packages:\\n  * - kali-tools-identify\\n  * - kali-tools-protect\\n  * - kali-tools-detect\\n  * - kali-tools-respond\\n  * - kali-tools-recover\\n\\nSigned-off-by: Re4son <3520622-re4son@users.noreply.gitlab.com>",
                        "state": "closed",
                        "created_at": "2024-12-16T21:19:29.399Z",
                        "updated_at": "2024-12-17T00:02:48.437Z",
                        "merged_by": null,
                        "merge_user": null,
                        "merged_at": null,
                        "closed_by": {
                            "id": 207986,
                            "username": "steev",
                            "public_email": "",
                            "name": "Steev Klimaszewski",
                            "state": "active",
                            "locked": false,
                            "avatar_url": "https://secure.gravatar.com/avatar/d3c8f0e40e02c6ddec00c00506b91ac12ae27bf7560dc316907ab7939f1d50fc?s=80&d=identicon",
                            "web_url": "https://gitlab.com/steev"
                        },
                        "closed_at": "2024-12-16T23:49:37.914Z",
                        "target_branch": "kali/master",
                        "source_branch": "kali-purple",
                        "user_notes_count": 0,
                        "upvotes": 0,
                        "downvotes": 0,
                        "author": {
                            "id": 12487845,
                            "username": "AbuTurkyi",
                            "public_email": "",
                            "name": "Abu2rkyi",
                            "state": "active",
                            "locked": false,
                            "avatar_url": "https://gitlab.com/uploads/-/system/user/avatar/12487845/avatar.png",
                            "web_url": "https://gitlab.com/AbuTurkyi"
                        },
                        "assignees": [],
                        "assignee": null,
                        "reviewers": [],
                        "source_project_id": null,
                        "target_project_id": 11903824,
                        "labels": [
                            "Role::Packager",
                            "Topic::Packaging"
                        ],
                        "draft": false,
                        "imported": false,
                        "imported_from": "none",
                        "work_in_progress": false,
                        "milestone": null,
                        "merge_when_pipeline_succeeds": false,
                        "merge_status": "cannot_be_merged",
                        "detailed_merge_status": "not_open",
                        "merge_after": null,
                        "sha": "516296340434bd9aa33796023353d0943f0418ba",
                        "merge_commit_sha": null,
                        "squash_commit_sha": null,
                        "discussion_locked": null,
                        "should_remove_source_branch": null,
                        "force_remove_source_branch": true,
                        "prepared_at": "2024-12-16T21:19:36.529Z",
                        "allow_collaboration": false,
                        "allow_maintainer_to_push": false,
                        "reference": "!35",
                        "references": {
                            "short": "!35",
                            "relative": "!35",
                            "full": "kalilinux/packages/kali-meta!35"
                        },
                        "web_url": "https://gitlab.com/kalilinux/packages/kali-meta/-/merge_requests/35",
                        "time_stats": {
                            "time_estimate": 0,
                            "total_time_spent": 0,
                            "human_time_estimate": null,
                            "human_total_time_spent": null
                        },
                        "squash": true,
                        "squash_on_merge": true,
                        "task_completion_status": {
                            "count": 0,
                            "completed_count": 0
                        },
                        "has_conflicts": true,
                        "blocking_discussions_resolved": true,
                        "approvals_before_merge": null
                    }
                ]
                """;
    @SuppressWarnings("all")
    private final String testBranch = """
            [
                {
                    "name": "kali-purple",
                    "commit": {
                        "id": "516296340434bd9aa33796023353d0943f0418ba",
                        "short_id": "51629634",
                        "created_at": "2023-02-07T12:12:06.000+07:00",
                        "parent_ids": [
                            "59d31c944a2bc662c640a3a266f58f58e1a43c63"
                        ],
                        "title": "Fixup 'Consistency with structure'",
                        "message": "Fixup 'Consistency with structure'\\n",
                        "author_name": "Arnaud Rebillout",
                        "author_email": "arnaudr@kali.org",
                        "authored_date": "2023-02-07T12:12:06.000+07:00",
                        "committer_name": "Arnaud Rebillout",
                        "committer_email": "arnaudr@kali.org",
                        "committed_date": "2023-02-07T12:12:06.000+07:00",
                        "trailers": {},
                        "extended_trailers": {},
                        "web_url": "https://gitlab.com/kalilinux/packages/kali-meta/-/commit/516296340434bd9aa33796023353d0943f0418ba"
                    },
                    "merged": false,
                    "protected": false,
                    "developers_can_push": false,
                    "developers_can_merge": false,
                    "can_push": false,
                    "default": false,
                    "web_url": "https://gitlab.com/kalilinux/packages/kali-meta/-/tree/kali-purple"
                },
                {
                    "name": "kali-update-2023",
                    "commit": {
                        "id": "d36137c14ed5b3629176a7d4bf5eb7d993f694e7",
                        "short_id": "d36137c1",
                        "created_at": "2023-02-10T18:01:21.000+07:00",
                        "parent_ids": [
                            "eec3eea4498f2c96bc010424a25c4a6241a9a4f2"
                        ],
                        "title": "WIP Work in progress including:",
                        "message": "WIP Work in progress including:\\n\\n- alternative structure for the low-level metapackages (ie.\\n  kali-linux-core and kali-system-*)\\n- merging of kali-system-cli and kali-system-core\\n- quite some changes in various Dependencies all over the place\\n",
                        "author_name": "Arnaud Rebillout",
                        "author_email": "arnaudr@kali.org",
                        "authored_date": "2023-02-10T17:58:23.000+07:00",
                        "committer_name": "Arnaud Rebillout",
                        "committer_email": "arnaudr@kali.org",
                        "committed_date": "2023-02-10T18:01:21.000+07:00",
                        "trailers": {},
                        "extended_trailers": {},
                        "web_url": "https://gitlab.com/kalilinux/packages/kali-meta/-/commit/d36137c14ed5b3629176a7d4bf5eb7d993f694e7"
                    },
                    "merged": false,
                    "protected": false,
                    "developers_can_push": false,
                    "developers_can_merge": false,
                    "can_push": false,
                    "default": false,
                    "web_url": "https://gitlab.com/kalilinux/packages/kali-meta/-/tree/kali-update-2023"
                }
            ]
            """;
    @SuppressWarnings("all")
    private final String testCommit = """
            [
                {
                    "id": "516296340434bd9aa33796023353d0943f0418ba",
                    "short_id": "51629634",
                    "created_at": "2023-02-07T12:12:06.000+07:00",
                    "parent_ids": [
                        "59d31c944a2bc662c640a3a266f58f58e1a43c63"
                    ],
                    "title": "Fixup 'Consistency with structure'",
                    "message": "Fixup 'Consistency with structure'\\n",
                    "author_name": "Arnaud Rebillout",
                    "author_email": "arnaudr@kali.org",
                    "authored_date": "2023-02-07T12:12:06.000+07:00",
                    "committer_name": "Arnaud Rebillout",
                    "committer_email": "arnaudr@kali.org",
                    "committed_date": "2023-02-07T12:12:06.000+07:00",
                    "trailers": {},
                    "extended_trailers": {},
                    "web_url": "https://gitlab.com/kalilinux/packages/kali-meta/-/commit/516296340434bd9aa33796023353d0943f0418ba"
                },
                {
                    "id": "59d31c944a2bc662c640a3a266f58f58e1a43c63",
                    "short_id": "59d31c94",
                    "created_at": "2023-02-07T10:03:34.000+07:00",
                    "parent_ids": [
                        "551807a4ed325d441dd58e946f3b986e3808c23a"
                    ],
                    "title": "Fix typos: i3-gaps, forensics, netsniff-ng",
                    "message": "Fix typos: i3-gaps, forensics, netsniff-ng\\n",
                    "author_name": "Arnaud Rebillout",
                    "author_email": "arnaudr@kali.org",
                    "authored_date": "2023-02-02T17:37:32.000+07:00",
                    "committer_name": "Arnaud Rebillout",
                    "committer_email": "arnaudr@kali.org",
                    "committed_date": "2023-02-07T10:03:34.000+07:00",
                    "trailers": {},
                    "extended_trailers": {},
                    "web_url": "https://gitlab.com/kalilinux/packages/kali-meta/-/commit/59d31c944a2bc662c640a3a266f58f58e1a43c63"
                }
            ]
            """;

    @Test
    void testCompressMembers() {

        List<Member> members = service.compressMembers(testMember);

        assertEquals(2, members.size());

        Member first = members.getFirst();
        assertEquals(1, first.id());
        assertEquals("john", first.username());
        assertEquals("john@example.com", first.publicEmail());
        assertEquals(30, first.accessLevel());
        assertEquals("https://gitlab.com/john", first.webUrl());

        Member second = members.get(1);
        assertEquals(2, second.id());
        assertEquals("alice", second.username());
    }

    @Test
    void testCompressMergeRequests() {
        List<MergeRequest> mergeRequests = service.compressMergeRequests(testMR);

        assertEquals(2, mergeRequests.size());

        MergeRequest first = mergeRequests.getFirst();
        assertEquals(385191603, first.id());
        assertEquals(36, first.iid());
        assertEquals(3924854, first.authorId());
        assertEquals("Fix missing packages for watches", first.title());
        assertEquals("merged", first.state());
        assertEquals("kali/master", first.targetBranch());
        assertEquals("2025-05-22T00:49:24.384Z", first.createdAt().toString());
        assertEquals("2025-05-22T07:24:00.423Z", first.mergedAt().toString());
    }
    @Test
    void testCompressMergeRequests_empty() {
        List<MergeRequest> mergeRequests = service.compressMergeRequests("[]");
        assertTrue(mergeRequests.isEmpty());
    }

    @Test
    void testCompressBranches() {
        List<Branch> branches = service.compressBranches(testBranch);

        assertEquals(2, branches.size());

        Branch first = branches.getFirst();
        assertEquals("kali-purple", first.name());
        assertEquals("51629634", first.lastCommit().shortId());
        assertEquals("https://gitlab.com/kalilinux/packages/kali-meta/-/tree/kali-purple", first.webUrl());
    }

    @Test
    void testCompressCommits(){
        List<Commit> commits = service.compressCommits(testCommit);

        assertEquals(2, commits.size());

        Commit first = commits.getFirst();
        assertEquals("51629634", first.shortId());
        assertEquals("https://gitlab.com/kalilinux/packages/kali-meta/-/commit/516296340434bd9aa33796023353d0943f0418ba", first.webUrl());

        Commit second = commits.get(1);
        assertEquals("59d31c94", second.shortId());
    }
}
