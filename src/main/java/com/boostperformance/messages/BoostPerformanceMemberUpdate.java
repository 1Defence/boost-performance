package com.boostperformance.messages;

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.runelite.client.party.messages.PartyMemberMessage;


@Value
@EqualsAndHashCode(callSuper = true)
public class BoostPerformanceMemberUpdate extends PartyMemberMessage
{
    boolean userJoining;
    boolean userLeaving;
    boolean userRequestingSync;
    String name;
}
