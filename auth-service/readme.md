to get access token for user ```curl XPOST -u demo:demo localhost:9090/oauth/token -d grant_type=password -d username=lvasek@domain.com -d password=password```   
to get access token for client ```curl XPOST -u demo:demo localhost:9090/oauth/token -d grant_type=client_credentials```   
to get user details ```curl u demo:demo localhost:9090/users/me -H 'Authorization: Bearer <token>'```   
