# Home Owners Association Application

This project addresses the Home-Owners-Association scenario. 

It contains three microservices:
- authentication-microservice
- association-microservice
- voting-microservice

The `authentication-microservice` is responsible for registering new users and authenticating current ones. After successful authentication, this microservice will provide a JWT token which can be used to bypass the security on the `association-microservice` and `voting-microservice`. This token contains the *UserID* of the user that authenticated. 

The `association-microservice` is responsible for all the logic regarding associations, so creating / joining, as well as partaking in an association's features. It includes the association's notice board, history, rules, council as well as rule reports.  

The `voting-microservice` is responsible for elections and rule votes. It handles everything from creating them to finishing them and sending their results. It's connected to the `association-microservice` in the sense that the latter sends it requests to start votes. Additionally, all the results from the `voting-microservice` are sent back to the association in question.


## Running the microservices

You can run the three microservices individually by starting the Spring applications. 
Then, you can use *Postman* to perform the different requests:
Each request has a RequestModel associated to it. Those can be found in the `LEGEND` section.

#### The following requests are available to the users:

#### `authentication-microservice: port number 8081`

- `/register` Register a new user into the system. [RegistrationRequestModel]
- `/authenticate` Authenticate a user. Gives a token which can be used to access the other microservices. [AuthenticationRequestModel]
- `/changepass` Changes a user's password. [UpdatePasswordRequestModel]

#### `association-microservice: port number 8084`

- `/association:`
  - `/create-association` Create a new association. [CreateAssociationRequestModel]
  - `/get-association-ids` Get all the association names and their respective IDs. [None]
  - `/get-association` Get information about an association. [AssociationRequestModel]
  - `/join-association` Join an association. [JoinAssociationRequestModel]
  - `/leave-association` Leave an association. [UserAssociationRequestModel]
  - `/verify-council-member` Check if a user is in the council of an association. [UserAssociationRequestModel]
  - `/report` Report a user in an association. [ReportModel]
  - `/get-rules` Get the rules of an association. [AssociationRequestModel]
  - `/get-history` Get the history of an association. [AssociationRequestModel]
  - `/display-notifications` Retrieve a user's notifications from an association. [UserAssociationRequestModel]
  - `/dismiss-notifications` Dismiss a user's notifications from an association [UserAssociationRequestModel]


- `/activities`
  - `/{associationId}/noticeboard` Get all the available activities of an association. [path variables][AssociationRequestModel]
  - `/{associationId}/{publisherId}` Add a new activity for an association. [path variables]
  - `/noticeboard/{activityId}` Get an activity corresponding to its ID. [path variables]
  - `/addInterested/{activityId}/{userId}` Add a user to an activity's list of interested people. [path variables]
  - `/addParticipating/{activityId}/{userId}` Add a user to an activity's list of participating people. [path variables]
  - `/removeInterested/{activityId}/{userId}` Remove a user from an activity's list of interested people. [path variables]
  - `/removeParticipating/{activityId}/{userId}` Remove a user from an activity's list of participating people [path variables]


- `/election`
  - `/create-election` Start a new election for an association. [httpServlet]

- `/rules`
  - `/propose` Vote for a rule to be enabled. [httpServlet]

#### `voting-microservice: port number 8083`
- `/election`
  - `/get-candidates` See the candidates in an election. [UserAssociationRequestModel]
  - `/apply-for-candidate` Candidate for an association's board. [UserAssociationRequestModel]
  - `/cast-vote` Vote for a candidate in an election. [ElectionVoteRequestModel]
  
- `/rule-voting`
  - `/cast-vote` Vote for a rule. [RuleVoteRequestModel]
  - `/get-pending-votes` See a user's options in ongoing votes in an association, as well as his current choices. [UserAssociationRequestModel]

#### Legend:
- RegistrationRequestModel : `String` userId, `String` password
- AuthenticationRequestModel : `String` userId, `String` password
- UpdatePasswordRequestModel : `String` userId, `String` password, `String` newPassword
- AssociationRequestModel : `int` associationId
- CreateAssociationRequestModel : `String` name, `String` country, `String` city, `String` description, `int` councilNumber
- JoinAssociationRequestModel : `int` associationId, `String` userId, `String` country, `String` city, `String` street, `String` houseNumber, `String` postalCode
- ElectionVoteRequestModel : `int` associationId, `String` voterId, `String` candidateId
- RuleAmendmentRequestModel : `int` associationId, `String` userId, `String` rule, `String` amendment
- RuleVoteRequestModel : `int` associationId, `String` userId, `String` rule
- UserAssociationRequestModel : `int` associationId, `String` userId 

- ReportModel : `int` associationId, `String` reporterId, `String` violatorId, `String` rule
- ActivityRequestModel : `String` eventName, `String` description, `Date` startingDate, `Date` expirationDate