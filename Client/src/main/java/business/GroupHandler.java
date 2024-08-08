package business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupHandler {
    private final Map<String, List<String>> groups = new ConcurrentHashMap<>();
    private final Map<String, List<String>> sentInvitesNicknamesForGroup = new ConcurrentHashMap<>();
    private final Map<String,String> receivedInvitesGroupIpPair = new ConcurrentHashMap<>(); // pair <Group Name, ip from person who invited me

    public void addGroup(String groupName, List<String> members) {
        groups.put(groupName, members);
    }

    public void addNicknameInPendingGroup(String groupNickname, String receiver) {
        if (!groups.containsKey(groupNickname)) {
            if (!sentInvitesNicknamesForGroup.containsKey(groupNickname)) {
                sentInvitesNicknamesForGroup.put(groupNickname, new ArrayList<>());
            }
            List<String> pendingIps = sentInvitesNicknamesForGroup.get(groupNickname);
            synchronized (pendingIps) {
                pendingIps.add(receiver);
            }
        } else {
            System.out.println("The group " + "doesn't exists");
        }
    }

    public boolean removeNicknamesInPending(String group, String sender) {
        return sentInvitesNicknamesForGroup.containsKey(group) && sentInvitesNicknamesForGroup.get(group).remove(sender);

    }

    public void addNewMember(String group, String ip) {
        if (groups.containsKey(group)) {
            groups.get(group).add(ip);
        } else {
            System.out.println(group + " doesn't exist");
        }
    }

    public List<String> getAllMembers(String group) {
        return groups.get(group);
    }

    public boolean existsGroup(String group) {
        return groups.containsKey(group);
    }

    public void addNewInvite(String group, String ip) {
        receivedInvitesGroupIpPair.put(group,ip);
    }


    public String removeReceivedInvite(String groupName) {
        return receivedInvitesGroupIpPair.remove(groupName);
    }
}
