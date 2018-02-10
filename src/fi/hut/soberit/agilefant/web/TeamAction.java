package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.annotations.PrefetchId;
import fi.hut.soberit.agilefant.business.TeamBusiness;
import fi.hut.soberit.agilefant.business.TeamBusiness.Call;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

@Component("teamAction")
@Scope("prototype")
public class TeamAction extends ActionSupport implements CRUDAction, Prefetching {

    private static final long serialVersionUID = -3334278151418035144L;

    @PrefetchId
    private int teamId;

    private Team team;
    
    private List<Team> teamList = new ArrayList<Team>();
    
    private Set<Integer> userIds = new HashSet<Integer>();
    
    private boolean usersChanged;
    
    private Set<Integer> productIds = new HashSet<Integer>();
    
    private boolean productsChanged;
    
    private Set<Integer> iterationIds = new HashSet<Integer>();
    
    private boolean iterationsChanged;

    @Autowired
    private TeamBusiness teamBusiness;
    
    @Autowired
    private UserBusiness userBusiness;

    /**
     * Create a new team.
     */
    public String create() {
        teamId = 0;
        team = new Team();
        team.setUsers(new ArrayList<User>());
        return Action.SUCCESS;
    }

    /**
     * Delete an existing team.
     */
    public String delete() {
        teamBusiness.delete(teamId);
        return Action.SUCCESS;
    }

    /**
     * Edit a team.
     */
    public String retrieve() {
        team = teamBusiness.retrieve(teamId);
        return Action.SUCCESS;
    }
    
    public String retrieveAll() {
        
        User loggedUser = getLoggedInUser();
        Boolean isAdmin = loggedUser.isAdmin();
        
        if (isAdmin) {
            teamList.addAll(this.teamBusiness.withUsers(new Call<Collection<Team>>() {
            	public Collection<Team> call() {
            		return teamBusiness.retrieveAll();
            	}
            }));
        } else {
            teamList.addAll(this.teamBusiness.withUsers(new Call<Collection<Team>>() {
            	public Collection<Team> call() {
            		return userBusiness.retrieve(getLoggedInUser().getId()).getTeams();
            	}
            }));
        }
        return Action.SUCCESS;
    }
    
    public String retrieveMyTeams() {        
        teamList.addAll(this.teamBusiness.withUsers(new Call<Collection<Team>>() {
        	public Collection<Team> call() {
        		return userBusiness.retrieve(getLoggedInUser().getId()).getTeams();
        	}
        }));
        return Action.SUCCESS;
    }

    /**
     * Store the team.
     */
    public String store() {
        Set<Integer> users = null;
        if (usersChanged) {
            users = userIds;
        }
        
        Set<Integer> products = null;
        if (productsChanged) {
            products = productIds;
        }
        
        Set<Integer> iterations = null;
        if (iterationsChanged) {
            iterations = iterationIds;
        }
        
        team = teamBusiness.storeTeam(team, users, products, iterations);
        return Action.SUCCESS;
    }



    public void initializePrefetchedData(int objectId) {
        team = teamBusiness.retrieve(objectId);
    }
    

    /*
     * List of autogenerated setters and getters
     */

    /**
     * @return the teamId
     */
    public int getTeamId() {
        return teamId;
    }

    /**
     * @param teamId
     *            the teamId to set
     */
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    /**
     * @return the team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * @param team
     *            the team to set
     */
    public void setTeam(Team team) {
        this.team = team;
    }


    public List<Team> getTeamList() {
        return teamList;
    }

    public void setTeamList(List<Team> teamList) {
        this.teamList = teamList;
    }

    public void setTeamBusiness(TeamBusiness teamBusiness) {
        this.teamBusiness = teamBusiness;
    }

    public void setUsersChanged(boolean usersChanged) {
        this.usersChanged = usersChanged;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }
    
    public void setProductsChanged(boolean productsChanged) {
        this.productsChanged = productsChanged;
    }

    public Set<Integer> getProductIds() {
        return productIds;
    }

    public void setProductIds(Set<Integer> productIds) {
        this.productIds = productIds;
    }

    public void setIterationsChanged(boolean iterationsChanged) {
        this.iterationsChanged = iterationsChanged;
    }

    public Set<Integer> getIterationIds() {
        return iterationIds;
    }

    public void setIterationIds(Set<Integer> iterationIds) {
        this.iterationIds = iterationIds;
    }
    
    /** USED WITH TeamRowController.js to confirm team deletion**/
    
    public String deleteTeamForm() {
        team = teamBusiness.retrieve(teamId);
        return Action.SUCCESS;
    }
    
    protected User getLoggedInUser() {
        return SecurityUtil.getLoggedUser();
    }
}
