package com.e2excel.myvoice;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.e2excel.myvoice.CustomDialogs.SIgnUp_confirm_password_dialogFragment;
import com.e2excel.myvoice.Retrofit.ApiService;
import com.e2excel.myvoice.Retrofit.RetroClient;
import com.e2excel.myvoice.pojo.Register.Register;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.e2excel.myvoice.MainActivity.Build_alert_dialog;

public class SignUp extends AppCompatActivity {


    private Dialog dialog;
    private TextView first_name, last_name, email_id, pass;
    private SharedPreferences pref, email_pref;
    private SharedPreferences.Editor editor, email_pref_editor;
    private ProgressDialog progressDialog;
    private TextView toc;
    private Button sign_up_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //getting coustom layout for toolbar-
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        View view = getSupportActionBar().getCustomView();

        ImageButton imageButton = (ImageButton) view.findViewById(R.id.action_bar_back);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pref = getSharedPreferences("MYVOICEAPP_PREF", MODE_PRIVATE);
        editor = pref.edit();

        email_pref = getSharedPreferences("MYVOICE_EMAIL_PREF", MODE_PRIVATE);
        email_pref_editor = email_pref.edit();

        dialog = new Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen);


        first_name = findViewById(R.id.f_name);
        first_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        last_name = findViewById(R.id.l_name);
        last_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        email_id = findViewById(R.id.email_id);
        pass = findViewById(R.id.password);
        sign_up_btn = findViewById(R.id.button5);


        // Set up progress before call
        progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        toc = findViewById(R.id.toc);
        SpannableString ss = new SpannableString(toc.getText().toString().trim());

        //for privacy policy
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                privacy_policy();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.dark_blue));
                ds.setUnderlineText(false);

            }
        };


        //for terms of use
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                TermsAndConditions();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.dark_blue));
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan1, 32, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 51, 63, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        toc.setText(ss);
        toc.setMovementMethod(LinkMovementMethod.getInstance());


        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("username", first_name.getText().toString().trim());
                b.putString("lastname", last_name.getText().toString().trim());
                b.putString("email_id", email_id.getText().toString().trim());
                b.putString("pass", pass.getText().toString().trim());


                SIgnUp_confirm_password_dialogFragment sIgnUp_confirm_password_dialogFragment = new SIgnUp_confirm_password_dialogFragment();

                sIgnUp_confirm_password_dialogFragment.setArguments(b);

                sIgnUp_confirm_password_dialogFragment.show(getSupportFragmentManager(), "signUpConfirmPassword");


            }
        });

    }


    //sign_up process function --RV



    //TERMS OF USE BUTTON DIALOG POPUP
    public void TermsAndConditions() {

        TextView pop_up_text;
        Button agree_btn;

        dialog.setContentView(R.layout.popup_view_layout);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        agree_btn = dialog.findViewById(R.id.agree_button);
        pop_up_text = dialog.findViewById(R.id.pop_up_text);

        pop_up_text.setText(Html.fromHtml("<!-- #######  YAY, I AM THE SOURCE EDITOR! #########-->\n" +
                "<p><a name=\"__DdeLink__174_1275638368\"></a> <span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">&copy; COPYRIGHT 2019 E2Excel LLC. ALL RIGHTS RESERVED.<br /> Last Updated on March 23, 2019</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>YOUR ACCEPTANCE OF THE TERMS OF USE.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">This is an agreement between you and E2Excel LLC (&ldquo;E2Excel&rdquo;). This agreement sets forth the terms and conditions which govern your use of our mobile app &ldquo;MyVoice&rdquo; and of the Content (as defined below) and services made available on or through the App (collectively, the &ldquo;Services&rdquo;). PLEASE READ THESE TERMS AND CONDITIONS OF USE (the &ldquo;Terms of Use&rdquo;) CAREFULLY BEFORE ACCESSING ANY OF THE APP AND USING THE SERVICES AS THEY CONSTITUTE A LEGALLY BINDING CONTRACT BETWEEN YOU AND E2Excel AND GOVERN YOUR ACCESS TO AND USE OF THE APP AND/OR SERVICES. BY ACCESSING OR USING THE APP AND/OR SERVICES IN ANY MANNER, YOU INDICATE YOUR UNCONDITIONAL ACCEPTANCE OF (1) THESE TERMS OF USE, (2)<span style=\"color: #000000;\"> OUR PRIVACY POLICY</span>; AND (3) ANY OTHER POLICIES, LEGAL NOTICES, or CONDITIONS THAT E2EXCEL MAY POST ON ITS WEB (WWW.E2EXCEL.COM) OR IN THE APP, AS APPLICABLE. IF YOU DO NOT AGREE TO THESE TERMS OF USE OR THE PRIVACY POLICY, PLEASE DO NOT USE THE SERVICES AND EXIT NOW. WE RESERVE THE RIGHT, IN OUR SOLE DISCRETION, TO UPDATE OR REVISE THESE TERMS OF USE FROM TIME TO TIME. YOUR CONTINUED USE OF THE APP AND/OR SERVICES NOW, OR FOLLOWING THE POSTING OR NOTICE OF ANY CHANGES TO THE TERMS OF USE OR PRIVACY POLICY WILL INDICATE YOUR ACCEPTANCE OF THOSE CHANGES.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>1. CONTENT MADE AVAILABLE ON OR THROUGH THE APP AND YOUR USE OF CONTENT.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>A. Description of Content.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">The app contains a wide variety of Content (defined below), whether (1) proprietary to E2Excel, or (2) proprietary to third parties. <strong>&ldquo;Content&rdquo;</strong> includes, but is not limited to text, data, files, documents, quesetons, reports, software, scripts, layout, design, function and &ldquo;look and feel,&rdquo; graphics, images, audio, videos, audiovisual combinations, interactive features and any other materials that you may view or access through the App. </span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>B. Proprietary Rights.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">You acknowledge and agree that all Content, whether publicly posted or privately transmitted, as well as all derivative works thereof, are owned by E2Excel or other parties that have licensed their material to E2Excel, and are protected by copyright, trademark, and other intellectual property laws. Except as specifically permitted herein, Content may not be copied, reproduced, republished, uploaded, posted, transmitted, or distributed in any way, including by e-mail or other electronic means, without the prior consent of E2Excel or such third-party that owns the Content.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>C. Restrictions on Your Use of the App.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">The following restrictions apply to your use of the App:</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">a. You may access the app for your information and use solely as intended through the provided functionality of the app and as permitted under this Agreement. You shall not use the app in any manner that infringes our or any third party's intellectual property rights, or other rights in or to the Content. All rights not expressly granted herein by E2Excel and/or its licensors to you are reserved by E2Excel and/or its licensors.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">b. You agree not to circumvent, disable or otherwise interfere with security-related features of the App and/or Services that prevent or restrict the use of any Content and not to alter, remove, or falsify any attributions or other proprietary designations of origin or source of any Content appearing on the App or contained in a file that is uploaded to the App.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>D. Disclaimer.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">Content is provided to You AS IS. You understand that we do not guarantee the accuracy, safety, integrity or quality of Content and you hereby agree that you must evaluate and bear all risks associated with the use of any Content, including any reliance on the Content, integrity, and accuracy of such Content.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>2. ACCESS TO AND USE OF THE MyVoice APP.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>A. Security and Passwords.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">Access to the MyVoice app is enabled only by User ID and Password. You shall maintain your User ID and Password in strict confidence. In no event shall you share your User ID or Password with any third party or allow another person to access the MyVoice App using your User ID and Password. You shall immediately notify E2Excel if you have any reason to believe that your User ID or Password has been lost or compromised or misused in any way. You are fully and solely responsible for any and all use of the MyVoice app, as applicable, using your User ID and Password. E2Excel reserves the right to revoke or deactivate your User ID and Password at any time if E2Excel determines that you have violated the terms of this Agreement.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>3. RESTRICTIONS ON YOUR USE OF THE APP/SERVICES.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">You agree that you shall access and use the App, including the Content made available on or through the App, in a manner consistent with all applicable professional and ethical standards and requirements, local, state, and national laws and regulations, and otherwise in accordance with this Agreement and the rules, policies and procedures established by E2Excel for use of the App. You agree that you shall not (and you agree not to allow any third party to): (i) access or use the App and/or Services in an unlawful way or for an unlawful or illegitimate purpose or in any manner that contravenes this Terms of Use and our Privacy Policy; (ii) attempt to disrupt the operation of the App/Services through use of methods such as viruses, Trojan horses, worms, time bombs, denial of service attacks, flooding or spamming; (iii) attempt, through any means, to gain unauthorized access to the App/Services and/or any computer systems or networks, through hacking, password mining or any other means; (iv) impersonate any person or entity, or falsely state or otherwise misrepresent your affiliation with a person or entity; or (v) use any robot, spider, site search/retrieval application, or other device to access, retrieve or index any portion of the App/Services/Content for any purpose. Systematic retrieval of data or other content from the App to create or compile, directly or indirectly, a collection, compilation, database or directory without our prior written permission is prohibited.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>4. TRADEMARKS/SERVICE MARKS, LOGOS, SLOGANS AND TAGLINES.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">All trademarks, service marks, logos, slogans and taglines displayed on the App are the property of E2Excel or its respective owners and nothing contained herein should be construed as granting any license or right to use any trademarks, service marks, logos, slogans or taglines displayed on the App without the express written permission of E2Excel, or such third-party that may own the trademark, service mark, logo, slogan or tagline.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>5. OPERATION/RECORDS RETENTION.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">You understand that the App/Services are provided as a courtesy to you and that we may modify, suspend or terminate all or a portion of the App/Services at any time in our discretion without prior notice to you. E2Excel reserves complete and sole discretion with respect to the operation of the App. E2Excel may, among other things withdraw, suspend or discontinue any functionality or feature of the App. E2Excel reserves the right to maintain, delete or destroy all communications and materials posted or uploaded to the App pursuant to its internal record retention and/or destruction policies as such policies may be amended.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>6. TERMINATION/EXCLUSION.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">We reserve the right, in our sole discretion, to revoke, terminate or suspend any privileges associated with accessing our App or Services for any reason or for no reason whatsoever, including improper use of the App or failure to comply with these Terms of Use, creating a risk or legal exposure for E2Excel, or when we are required to do so by law, and to take any other action we deem appropriate. You agree that E2Excel shall not be liable to you or any third party for any termination of your access to the App. Where appropriate, we will notify you about your account suspension or termination.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>7. VISITOR/USER SUGGESTIONS.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">You acknowledge and agree that any questions, comments, suggestions, ideas, feedback, or other information provided by you to E2Excel (&ldquo;Comments&rdquo;) are not confidential and you hereby grant to E2Excel a worldwide, perpetual, irrevocable, royalty-free license to reproduce, display, perform, distribute, publish, modify, edit or otherwise use such Comments as it deems appropriate, for any and all commercial or non-commercial purposes, in its sole discretion.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>8. LINKS TO THIRD PARTY SITES, APPS AND SERVICES; NO IMPLIED ENDORSEMENT.</strong></span></span></p>\n" +
                "<p><a name=\"_GoBack\"></a> <span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">The App may contain links to other web sites and systems owned by third parties, including sites maintained by our third-party service providers. Please note that when you click on any of these links, you are entering another web site for which E2Excel has no responsibility or control. You agree that E2Excel shall not be responsible for any loss or damage of any sort incurred as a result of any such links or as the result of the presence of such links on our App. In no event shall any reference to any third party, third party website or third-party product or service be construed as an approval or endorsement by us of that third party, third party website or of any product or service provided by a third party.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>9. DISCLAIMER OF WARRANTY.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">ACCESS TO THE APPS, SITES AND THE SERVICES IS PROVIDED &ldquo;AS IS&rdquo; AND &ldquo;AS AVAILABLE&rdquo; &ldquo;WITH ALL FAULTS&rdquo; AND WITHOUT ANY WARRANTY OF ANY KIND, EXPRESS OR IMPLIED. TO THE FULLEST EXTENT PERMISSIBLE PURSUANT TO APPLICABLE LAW, E2EXCEL AND ITS LICENSORS DISCLAIM ALL WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES OF TITLE, MERCHANTIBILITY, AND FITNESS FOR A PARTICULAR PURPOSE, TITLE OR NON-INFRINGEMENT. WITHOUT LIMITING THE FOREGOING, NEITHER E2EXCEL NOR ITS LICENSORS WARRANT THAT ACCESS TO THE APP AND/OR THE SERVICES WILL BE UNINTERRUPTED OR ERROR-FREE, OR THAT DEFECTS, IF ANY, WILL BE CORRECTED; NOR DOES E2EXCEL AND ITS LICENSORS MAKE ANY REPRESENTATIONS ABOUT THE ACCURACY, RELIABILITY, CURRENCY, QUALITY, COMPLETENESS, USEFULNESS, PERFORMANCE, SECURITY, LEGALITY OR SUITABILITY OF THE APP/SERVICES. YOU EXPRESSLY AGREE THAT YOUR USE OF THE APP AND/OR SERVICES AND YOUR RELIANCE UPON THE CONTENT IS AT YOUR SOLE RISK. NEITHER E2EXCEL NOR ANY THIRD PARTY PROVIDERS, PARTNERS OR AFFILIATES WARRANT THAT THE APPS, SITES, THEIR SERVERS OR ANY E-MAIL SENT FROM E2EXCEL OR ANY THIRD PARTY PROVIDERS, PARTNERS OR AFFILIATES ARE FREE OF VIRUSES OR OTHER HARMFUL COMPONENTS.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>10. LIMITATION OF LIABILITY.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">WE ARE NOT LIABLE TO YOU OR ANY OTHER PERSON FOR ANY PUNITIVE, EXEMPLARY, CONSEQUENTIAL, INCIDENTAL, INDIRECT OR SPECIAL DAMAGES (INCLUDING, WITHOUT LIMITATION, ANY PERSONAL INJURY, LOST PROFITS, BUSINESS INTERRUPTION, LOSS OF PROGRAMS OR OTHER DATA ON YOUR COMPUTER OR OTHERWISE) ARISING FROM OR IN CONNECTION WITH YOUR USE OF THE APP AND/OR THE SERVICES, WHETHER UNDER A THEORY OF BREACH OF CONTRACT, NEGLIGENCE, STRICT LIABILITY, MALPRACTICE OR OTHERWISE, EVEN IF E2EXCEL OR ITS AFFILIATES OR THIRD PARTY PROVIDERS HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. YOU HEREBY RELEASE E2EXCEL AND HOLD E2EXCEL AND ITS PARENTS, SUBSIDIARIES, AFFILIATES, OR LICENSORS, AND THEIR OFFICERS, DIRECTORS, TRUSTEES, AFFILIATES, SUBCONTRACTORS, AGENTS AND EMPLOYEES, HARMLESS FROM ANY AND ALL CLAIMS, DEMANDS, AND DAMAGES OF EVERY KIND AND NATURE (INCLUDING, WITHOUT LIMITATION, ACTUAL, SPECIAL, INCIDENTAL AND CONSEQUENTIAL), KNOWN AND UNKNOWN, SUSPECTED AND UNSUSPECTED, DISCLOSED AND UNDISCLOSED, ARISING OUT OF OR IN ANY WAY CONNECTED WITH YOUR USE OF THE APP AND/OR THE SERVICES. YOU WAIVE THE PROVISIONS OF ANY STATE OR LOCAL LAW LIMITING OR PROHIBITING A GENERAL RELEASE.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>11. EXCLUSIVE REMEDY.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">IN THE EVENT OF ANY PROBLEM WITH THE APP AND/OR THE SERVICES, YOU AGREE THAT YOUR SOLE AND EXCLUSIVE REMEDY IS TO CEASE USING THE APP AND/OR THE SERVICES. UNDER NO CIRCUMSTANCES SHALL E2EXCEL, ITS AFFILIATES, OR LICENSORS BE LIABLE IN ANY WAY FOR YOUR USE OF THE APP AND/OR THE SERVICES, INCLUDING, BUT NOT LIMITED TO, ANY ERRORS OR OMISSIONS IN CONTENT, ANY INFRINGEMENT BY THE CONTENT OF THE INTELLECTUAL PROPERTY RIGHTS OR OTHER RIGHTS OF THIRD PARTIES, OR FOR ANY LOSS OR DAMAGE OF ANY KIND INCURRED AS A RESULT OF THE USE OF THE APP AND/OR THE SERVICES OR ANY OF THE CONTENT. Certain states and/or jurisdictions do not allow the exclusion of implied warranties or limitation of liability for incidental or consequential damages, so the exclusions set forth above may not apply to you.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>12. INDEMNIFICATION.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">You agree to indemnify, hold harmless, and defend E2Excel and its licensors, suppliers, officers, directors, employees, agents, affiliates, subsidiaries, successors and assigns (collectively \"Indemnified Parties\") from and against any and all liability, loss, claim, damages, expense, or cost (including but not limited to attorneys' fees), incurred by or made against the Indemnified Parties in connection with any claim arising from or related to your use of the App and/or the Services, or any breach or violation of these Terms of Use by you. You agree to fully cooperate as reasonably required by an Indemnified Party(ies). Each Indemnified Party may, at its own expense, assume the exclusive defense and control of any matter for which it is indemnified hereunder. You shall not settle any matter without the consent of the applicable Indemnified Party.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>13. MISCELLANEOUS.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">THE FORMATION, INTERPRETATION AND PERFORMANCE OF THIS AGREEMENT AND ANY DISPUTES ARISING OUT OF IT SHALL BE GOVERNED BY THE SUBSTANTIVE AND PROCEDURAL LAWS OF THE COMMOMWEALTH OF MASSACHUSETTS WITHOUT REGARD TO ITS RULES ON CONFLICTS OR CHOICE OF LAW. You acknowledge that a violation or attempted violation of any of these Terms of Use will cause such damage to E2Excel as will be irreparable, the exact amount of which would be impossible to ascertain and for which there will be no adequate remedy at law. Accordingly, you agree that E2Excel shall be entitled as a matter of right to an injunction issued by any court of competent jurisdiction, restraining such violation or attempted violation of these terms and conditions by you, or your affiliates, partners, or agents, as well as to recover from you any and all costs and expenses sustained or incurred by E2Excel in obtaining such an injunction, including, without limitation, reasonable attorney's fees. You agree that no bond or other security shall be required in connection with such injunction. In the event any of the terms or provisions of this Agreement are determined to be invalid, illegal or otherwise unenforceable, the same shall not affect the other terms or provisions hereof or the whole of this Agreement, but such term or provision shall be deemed modified to the extent necessary to render such term or provision enforceable. A waiver of or failure to insist on performance of any of the terms of the Agreement will not operate as a waiver of any subsequent default whether of the same or similar nature.</span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\"><strong>14. OTHER.</strong></span></span></p>\n" +
                "<p><span style=\"font-family: Times, serif;\"><span style=\"font-size: small;\">These terms make up the entire agreement between you and E2Excel regarding your use of our app and services. They supersede any prior agreements.</span></span></p>\n" +
                "<p>&nbsp;</p>"));
        agree_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void privacy_policy() {

        TextView pop_up_text;
        Button agree_btn;

        dialog.setContentView(R.layout.popup_view_layout);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        agree_btn = dialog.findViewById(R.id.agree_button);
        agree_btn.setText("I agree Privacy Policy");

        TextView title = dialog.findViewById(R.id.title);
        title.setText("Privacy Policy");

        pop_up_text = dialog.findViewById(R.id.pop_up_text);

       /* pop_up_text.setText(Html.fromHtml("<pre><b>General App Usage</b><br>" +
                "<br>" +
                "Last Revised: May 16, 2018<br>" +
                "<br>" +
                "Welcome to www.lorem-ipsum.info. This site is provided as a service to our visitors and may be used for informational purposes only. Because the Terms and Conditions contain legal obligations, please read them carefully.<br>" +
                "<br>" +
                "<b>1. YOUR AGREEMENT</b><br>" +
                "<br>" +
                "By using this Site, you agree to be bound by, and to comply with, these Terms and Conditions. If you do not agree to these Terms and Conditions, please do not use this site.<br>" +
                "<br>" +
                "PLEASE NOTE: We reserve the right, at our sole discretion, to change, modify or otherwise alter these Terms and Conditions at any time. Unless otherwise indicated, amendments will become effective immediately. Please review these Terms and Conditions periodically. Your continued use of the Site following the posting of changes and/or modifications will constitute your acceptance of the revised Terms and Conditions and the reasonableness of these standards for notice of changes. For your information, this page was last updated as of the date at the top of these terms and conditions.<br>" +
                "<br>" +
                "<b>2. PRIVACY</b><br>" +
                "<br>" +
                "Please review our Privacy Policy, which also governs your visit to this Site, to understand our practices.<br>" +
                "<br>" +
                "<b>3. LINKED SITES</b><br>" +
                "<br>" +
                "This Site may contain links to other independent third-party Web sites (\"Linked Sites”). These Linked Sites are provided solely as a convenience to our visitors. Such Linked Sites are not under our control, and we are not responsible for and does not endorse the content of such Linked Sites, including any information or materials contained on such Linked Sites. You will need to make your own independent judgment regarding your interaction with these Linked Sites.<br>" +
                "<br>" +
                "<b>4. FORWARD LOOKING STATEMENTS</b><br>" +
                "<br>" +
                "All materials reproduced on this site speak as of the original date of publication or filing. The fact that a document is available on this site does not mean that the information contained in such document has not been modified or superseded by events or by a subsequent document or filing. We have no duty or policy to update any information or statements contained on this site and, therefore, such information or statements should not be relied upon as being current as of the date you access this site.<br></pre>"));

*/
        pop_up_text.setText(Html.fromHtml("<!-- #######  YAY, I AM THE SOURCE EDITOR! #########-->\n" +
                "<p class=\"western\" align=\"justify\"><span style=\"font-size: xx-small;\">&copy; COPYRIGHT 2019 E2Excel, LLC. ALL RIGHTS RESERVED.</span></p>\n" +
                "<p class=\"western\" align=\"justify\"><span style=\"font-size: xx-small;\">Last Updated on March 23, 2019</span></p>\n" +
                "<p class=\"western\" align=\"justify\"><span style=\"font-size: xx-small;\"><strong>SUMMARY. </strong></span><span style=\"font-size: xx-small;\">E2Excel, LLC (</span><span style=\"font-size: xx-small;\"><strong>&ldquo;E2Excel&rdquo;, &ldquo;we,&rdquo; &ldquo;us,&rdquo; or &ldquo;our&rdquo;</strong></span><span style=\"font-size: xx-small;\">)</span><span style=\"color: #373d48;\"><span style=\"font-size: xx-small;\"> provides this Privacy Policy to inform Registered Users (as defined below) of our policies and procedures regarding the collection, use and disclosure of Personal Information (as defined below) received from registered users of this mobile application, called the &ldquo;MyVoice&rdquo; app (the </span></span><span style=\"color: #373d48;\"><span style=\"font-size: xx-small;\"><strong>\"App\"</strong></span></span><span style=\"color: #373d48;\"><span style=\"font-size: xx-small;\">). </span></span><span style=\"font-size: xx-small;\">It is your responsibility to review this Privacy Policy carefully, especially before providing any Personal Information through the App. This Privacy Policy is incorporated into, and part of, our </span><span style=\"font-size: xx-small;\"><strong>Terms of Use</strong></span>&nbsp;<span style=\"font-size: xx-small;\">which govern your access and use of this App and the Content in general. Unless otherwise defined herein, capitalized terms shall have the meanings assigned to such terms in the Terms of Use. By accessing this App and/or using the Services, </span><span style=\"color: #373d48;\"><span style=\"font-size: xx-small;\">you agree to the terms of this Privacy Policy, as they may be amended by us from time to time.</span></span></p>\n" +
                "<p class=\"western\" align=\"justify\"><span style=\"font-size: xx-small;\"><strong>INFORMATION WE COLLECT FROM YOU. </strong></span><span style=\"font-size: xx-small;\">We collect your Personal Information via the App only when you expressly provide it to us. \"Personal Information\" is information about you that is personally identifiable to you and includes your First Name, Last Name, Phone Number and Email Address. By submitting Personal Information through the App, you agree to the terms of this Privacy Policy and you expressly consent to the processing of your Personal Information according to this Privacy Policy.</span></p>\n" +
                "<p class=\"western\" align=\"justify\"><span style=\"font-size: xx-small;\"><strong>INFORMATION WE TRACK. </strong></span><span style=\"color: #000000;\"><span style=\"font-size: xx-small;\">As part of operating the App, we gather certain App usage data, such as how many questions you have answered in a given period of time via the App; your answers to questions put forth in the App, and how frequently you access the App. This tracking may be accomplished through various technologies, including but not limited to through the use of cookies and Artificial Intelligence &ldquo;AI&rdquo;. </span></span><span style=\"font-size: xx-small;\">We may use the information to provide market research data through E2Excel&rsquo;s market research services, to </span><span style=\"color: #373d48;\"><span style=\"font-size: xx-small;\">better understand how you interact with the App, to monitor aggregate usage by our Registered Users, to improve the App and our Services</span></span><span style=\"font-size: xx-small;\">, and in certain cases, we may use this information to deny service in accordance with our Terms of Use. </span></p>\n" +
                "<p class=\"western\" align=\"justify\"><span style=\"font-size: xx-small;\"><strong>HOW YOUR PERSONAL INFORMATION IS COLLECTED. </strong></span><span style=\"font-size: xx-small;\">In order to access the App and participate in its purpose, you will be required to register as a user of the App (each, a &ldquo;Registered User) and to submit the following Personal Information to us as part of the registration process: your First and Last Name, Email Address (which is your User Name) and a password you have created to access the App. You can access, update and correct the Personal Information you provide to us via the Settings &gt; Profile section of the App at anytime by logging into your Registered User account. As any time, you will also have the opportunity to delete your account, which will delete your personally identifiable information, namely, your First and Last Name, Email Address (User Name) and Password.</span></p>\n" +
                "<p class=\"western\" align=\"justify\"><span style=\"font-size: xx-small;\"><strong>HOW WE USE YOUR PERSONAL INFORMATION.</strong></span><span style=\"font-size: xx-small;\"> The Personal Information E2Excel gathers from you may be used for the following purposes: (i) to provide the App to you; (2) to respond to your requests and inquiries, (3) to manage your Registered User account; and (4) to contact you when necessary</span><span style=\"color: #000000;\"><span style=\"font-size: xx-small;\">. </span></span><span style=\"font-size: xx-small;\">Please understand that if you choose not to receive promotional correspondence from us, we may still contact you in connection with your relationship, activities, transactions and communications with us.</span><span style=\"font-size: xx-small;\"> Any and all uses of Personal Information by E2Excel comply with applicable laws</span><span style=\"font-size: xx-small;\">. </span></p>\n" +
                "<p class=\"western\" align=\"justify\"><span style=\"font-size: xx-small;\"><strong>USE OF COOKIES: </strong></span><span style=\"font-size: xx-small;\">Cookies are unique identifiers that we transfer to your device to enable our systems to recognize your device and provide features of our services, such as recommended survey questions. </span></p>\n" +
                "<p class=\"western\" align=\"justify\"><span style=\"font-size: xx-small;\"><strong>HOW WE SHARE YOUR PERSONAL INFORMATION. </strong></span><span style=\"font-size: xx-small;\"><span lang=\"en\">Except as otherwise expressly provided in this Privacy Policy, we will only share the Personal Information you provide us via the App: </span></span><span style=\"font-size: xx-small;\"><span lang=\"en\"><strong>(A)</strong></span></span><span style=\"font-size: xx-small;\"><span lang=\"en\"> as required by law or regulation or as we believe in good faith that </span></span><span style=\"font-size: xx-small;\">disclosure of such information is reasonably necessary to (1) satisfy any applicable law or regulation, (2) respond to claims and/or to comply with a judicial proceeding, court order, or legal process served on us, (3) to protect and defend our rights or property, and/or the rights or property of our Registered Users, or third parties, as permitted by law (4) detect, prevent, or otherwise address fraud, security or technical issues, (5) enforce our Terms of Use, (6) to contact you regarding any promotional offers or results of a promotion offered by E2Excel.</span></p>\n" +
                "<p class=\"western\" align=\"justify\"><a name=\"_GoBack\"></a> <span style=\"font-size: xx-small;\"><strong>USE OF ANONYOUS INFORMATION. </strong></span><span style=\"font-size: xx-small;\">We may create anonymous information records from your Personal Information by excluding information (such as your name, email, phone number) that makes the information personally identifiable to you. For clarity, anonymous information is information that does not permit the identification of individual persons. We may use this anonymous information for any purpose in our discretion.</span></p>\n" +
                "<p class=\"western\" align=\"justify\"><span style=\"font-size: xx-small;\"><strong>CHILDREN UNDER 18 YEARS OF AGE. </strong></span><span style=\"font-size: xx-small;\">You should be aware that this App is not intended for, or designed to attract, children under the age of 18 and as such, E2Excel </span><span style=\"font-size: xx-small;\">does not intentionally gather Personal Information from individuals who are under the age of 18. </span><span style=\"color: #373d48;\"><span style=\"font-size: xx-small;\">If we become aware that a child under 18 has provided us with Personal Information, we will delete such information from our files.</span></span></p>\n" +
                "<p class=\"western\" align=\"justify\"><span style=\"font-size: xx-small;\"><strong>SECURITY. </strong></span><span style=\"font-size: xx-small;\">E2Excel uses reasonable measures to maintain the security of your Personal Information. We use industry standard SSL (secure socket layer) encryption whenever we ask you to login to the App and when we display your Personal Information. However, no company, including E2Excel can fully eliminate security risks associated with the transmission of Personal Information through online transactions, and you do so at your own risk. </span></p>\n" +
                "<p class=\"western\" align=\"justify\"><span style=\"font-size: xx-small;\"><strong>PRIVACY POLICY UPDATES. </strong></span><span style=\"color: #373d48;\"><span style=\"font-size: xx-small;\">This Privacy Policy may be updated from time to time for any reason; each version will apply to information collected while it was in place. We will notify you of any material changes to our Privacy Policy by posting the new Privacy Policy on our App.</span></span><span style=\"font-size: xx-small;\"> You are urged to return to check the Privacy Policy for the latest updates prior to submitting any Personal Information through the App.</span></p>\n" +
                "<p class=\"western\" align=\"justify\"><span style=\"font-size: xx-small;\"><strong>HOW TO CONTACT US. </strong></span><span style=\"font-size: xx-small;\"><span lang=\"en\">If you have any questions or concerns about this Privacy Policy you may&nbsp;contact us&nbsp; via email at </span></span><span style=\"color: #237bd2;\"><a class=\"western\" href=\"mailto:info@e2excel.com\"><span style=\"font-size: xx-small;\"><span lang=\"en\">info@e2excel.com</span></span></a></span><span style=\"color: #000000;\"><span style=\"font-size: xx-small;\">.</span></span></p>"));


        String privacy_policy_text = pop_up_text.getText().toString().trim();
        int index = privacy_policy_text.indexOf("Terms of Use");
        int last_index = index + "Terms of Use".length();

        SpannableString ss = new SpannableString(privacy_policy_text);

        ClickableSpan c1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                TermsAndConditions();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.dark_blue));
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(c1, index, last_index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pop_up_text.setText(ss);
        pop_up_text.setMovementMethod(LinkMovementMethod.getInstance());


        Log.v("toc", pop_up_text.getText().toString().trim());

        agree_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


}
