/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ktw.awesome.security;

import com.google.api.client.util.Key;

/**
 *
 * @author KellyATWhiteley
 */
public class UserInfo {

    @Key
    public String issued_to;

    @Key
    public String audience;

    @Key
    public String user_id;

    @Key
    public String scope;

    @Key
    public Integer expires_in;

    @Key
    public String email;

    @Key
    public Boolean verified_email;

    @Key
    public String access_type;

}
