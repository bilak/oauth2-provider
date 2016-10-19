to get access token for user ```curl XPOST -u demo:demo localhost:9090/oauth/token -d grant_type=password -d username=lvasek@domain.com -d password=password```   
to get access token for client ```curl XPOST -u demo:demo localhost:9090/oauth/token -d grant_type=client_credentials```   
to get user details ```curl u demo:demo localhost:9090/users/me -H 'Authorization: Bearer <token>'```  

to re-authenticate ```curl -u demo:demo http://localhost:9090/users/906d7631-581c-44ce-b71b-a2331f5b58ec -H 'Authorization: Bearer <token for client>' -H 'X-USER-ID: 906d7631-581c-44ce-b71b-a2331f5b58ec'```