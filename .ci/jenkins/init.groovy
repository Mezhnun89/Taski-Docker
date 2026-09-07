import jenkins.model.Jenkins
import hudson.security.HudsonPrivateSecurityRealm
import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import hudson.plugins.git.GitSCM
import hudson.plugins.git.BranchSpec
import hudson.plugins.git.UserRemoteConfig
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
def j = Jenkins.get()
def realm = new HudsonPrivateSecurityRealm(false)
realm.createAccount("ci-admin", System.getenv("JENKINS_TEST_PASSWORD"))
j.setSecurityRealm(realm)
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
j.setAuthorizationStrategy(strategy)
j.setNumExecutors(2)
j.setSlaveAgentPort(-1)
SystemCredentialsProvider.getInstance().getCredentials().add(
    new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, "dockerhub-credentials",
        "Existing Docker Hub credential for this temporary lesson run",
        System.getenv("DOCKER_USER"), System.getenv("DOCKER_TOKEN")))
SystemCredentialsProvider.getInstance().save()
def job = j.createProject(WorkflowJob, "taski-sprint11")
def scm = new GitSCM([new UserRemoteConfig("https://github.com/Mezhnun89/taski.git", null, null, null)],
    [new BranchSpec("a5c3542081fdd91d5085794947150b7d3eab8669")], false, [], null, null, [])
job.setDefinition(new CpsScmFlowDefinition(scm, "Jenkinsfile"))
job.save()
j.save()
job.scheduleBuild2(0)
